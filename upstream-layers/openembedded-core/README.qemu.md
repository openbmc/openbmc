QEMU Emulation Targets
======================

To simplify development, the build system supports building images to
work with the QEMU emulator in system emulation mode. Several architectures
are currently supported in 32 and 64 bit variants:

* ARM (qemuarm + qemuarm64)
* x86 (qemux86 + qemux86-64)
* PowerPC (qemuppc only)
* MIPS (qemumips + qemumips64)

Use of the QEMU images is covered in the Yocto Project Reference Manual.
The appropriate MACHINE variable value corresponding to the target is given
in brackets.
