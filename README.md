# Mouse Highlight

A lightweight Java-based utility that highlights the mouse cursor position for better visibility during presentations and screen recordings. For work environments with security restrictions, it can be built from source following standard security protocols.

## Features

- **Visual Mouse Tracking**: Creates a yellow circle around your mouse cursor for enhanced visibility
- **Click Feedback**: The highlight circle shrinks when clicking to provide visual feedback
- **System Tray Integration**: Runs quietly in the system tray for easy access and management
- **Cross-Platform**: Works on Windows, macOS, and Linux systems
- **Lightweight**: Minimal resource usage

## Requirements

- Java 8 or higher
- Gradle (for building from source)

## Usage

1. Launch the application using one of the methods described above
2. A yellow circle will appear around your mouse cursor
3. The circle will shrink when you click to provide visual feedback
4. Access the application from the system tray icon to exit

## Technical Implementation

This application uses:

- **JNativeHook**: For global mouse event listening across the operating system
- **Java Swing**: For creating the transparent highlight window
- **AWT**: For system tray integration

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [JNativeHook](https://github.com/kwhat/jnativehook) for providing global keyboard and mouse hook functionality
- Icons from [Icons8](https://icons8.com/)