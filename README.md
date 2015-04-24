# ExtractAR
### Extract Android 'R.txt' resource list

This tool extracts an 'R.txt'-style resource list from a compiled Android application.
The input must be Java bytecode (use dex2jar if necessary). The output is compatible
with the 'R.txt' format used in Android archive (.aar) files.

    Usage: extractar <classpath-directory-or-jar> <fully-qualified-name-of-R-class>

> **WARNING:** This tool loads and initializes the target 'R' class (which means that
it executes untrusted code) without sandboxing it. A rogue class can attack you.
Decompile and analyze the class or drop privileges before invoking this tool.

Visit the XDA thread for details:
http://forum.xda-developers.com/showthread.php?t=3060854

ExtractAR is free software (GPLv3+)
