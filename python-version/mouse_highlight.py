from PyQt5 import QtWidgets, QtGui, QtCore
import sys
from pynput import mouse

class ColorTheme:
    BLUE = 'BLUE'
    YELLOW = 'YELLOW'
    GREEN = 'GREEN'
    PURPLE = 'PURPLE'
    PINK = 'PINK'
    ORANGE = 'ORANGE'

COLOR_MAP = {
    ColorTheme.BLUE:   [(100,181,246), (30,136,229)],
    ColorTheme.YELLOW: [(255,223,100), (255,193,7)],
    ColorTheme.GREEN:  [(129,199,132), (56,142,60)],
    ColorTheme.PURPLE: [(179,157,219), (123,31,162)],
    ColorTheme.PINK:   [(244,143,177), (194,24,91)],
    ColorTheme.ORANGE: [(255,183,77), (239,108,0)],
}

class OverlayWidget(QtWidgets.QWidget):
    def __init__(self, theme=ColorTheme.YELLOW):
        super().__init__()
        # Window setup
        self.setWindowFlags(
            QtCore.Qt.FramelessWindowHint |
            QtCore.Qt.WindowStaysOnTopHint |
            QtCore.Qt.Tool |
            QtCore.Qt.WindowTransparentForInput
        )
        self.setAttribute(QtCore.Qt.WA_TranslucentBackground)
        self.setAttribute(QtCore.Qt.WA_ShowWithoutActivating)
        self.setGeometry(QtWidgets.QApplication.desktop().geometry())

        # State
        self.cursor_pos = None
        self.pressed = False
        self.theme = theme

        # Mouse listener
        self.listener = mouse.Listener(on_click=self.on_click)
        self.listener.start()

        # Timer for cursor tracking
        self.timer = QtCore.QTimer(self)
        self.timer.timeout.connect(self.update_cursor_pos)
        self.timer.start(20)

        self.show()

    def setTheme(self, theme):
        self.theme = theme
        self.update()

    def update_cursor_pos(self):
        pos = QtGui.QCursor.pos()
        self.cursor_pos = (pos.x(), pos.y())
        self.update()

    def on_click(self, x, y, button, pressed):
        self.pressed = pressed
        self.update()

    # Qt built-in event: called automatically when widget needs to be repainted
    def paintEvent(self, event):
        qp = QtGui.QPainter(self)
        qp.setRenderHint(QtGui.QPainter.Antialiasing)
        if self.cursor_pos:
            colors = COLOR_MAP[self.theme]
            highlight_color = QtGui.QColor(*colors[0], 180)
            pressed_color = QtGui.QColor(*colors[1], 220)
            # Outer circle
            qp.setPen(QtCore.Qt.NoPen)
            qp.setBrush(highlight_color)
            qp.drawEllipse(QtCore.QPoint(*self.cursor_pos), 25, 25)
            # Inner circle when pressed
            if self.pressed:
                qp.setBrush(pressed_color)
                qp.drawEllipse(QtCore.QPoint(*self.cursor_pos), 17, 17)
        qp.end()

if __name__ == "__main__":
    app = QtWidgets.QApplication(sys.argv)
    overlay = OverlayWidget(theme=ColorTheme.YELLOW)

    # Tray icon setup
    tray_icon = QtWidgets.QSystemTrayIcon()
    icon = app.style().standardIcon(QtWidgets.QStyle.SP_ComputerIcon)
    tray_icon.setIcon(icon)
    tray_icon.setToolTip("Mouse Highlighter")
    tray_menu = QtWidgets.QMenu()

    # Add color theme actions
    theme_group = QtWidgets.QActionGroup(tray_menu)
    theme_group.setExclusive(True)
    theme_actions = {}
    for theme in COLOR_MAP.keys():
        action = tray_menu.addAction(theme.capitalize())
        action.setCheckable(True)
        if theme == overlay.theme:
            action.setChecked(True)
        theme_group.addAction(action)
        theme_actions[theme] = action
    def on_theme_selected():
        for theme, action in theme_actions.items():
            if action.isChecked():
                overlay.setTheme(theme)
                break
    theme_group.triggered.connect(lambda _: on_theme_selected())

    tray_menu.addSeparator()
    quit_action = tray_menu.addAction("Quit")
    quit_action.triggered.connect(app.quit)
    tray_icon.setContextMenu(tray_menu)
    tray_icon.show()

    sys.exit(app.exec_())
