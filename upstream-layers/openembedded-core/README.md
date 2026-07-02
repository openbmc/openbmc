OpenEmbedded-Core
=================

OpenEmbedded-Core is a layer containing the core metadata for current versions
of OpenEmbedded. It is distro-less (can build a functional image with
DISTRO = "nodistro") and contains only emulated machine support.

For information about OpenEmbedded, see the OpenEmbedded website:
<https://www.openembedded.org/>

The Yocto Project has extensive documentation about OE including a reference manual
which can be found at:
<https://docs.yoctoproject.org/>

Contributing
------------

Please refer to our contributor guide here: <https://docs.yoctoproject.org/dev/contributor-guide/>
for full details on how to submit changes.

For any files containing sorted lists (such as recipe maintainers), please ensure
alphabetical order is maintained using the C locale. For example, use:
`LC_ALL=C sort` to sort the list.

As a quick guide, patches should be sent to openembedded-core@lists.openembedded.org
The git command to do that would be:

```
git send-email -M -1 --to openembedded-core@lists.openembedded.org
```

Mailing list:
<https://lists.openembedded.org/g/openembedded-core>

Source code:
<https://git.openembedded.org/openembedded-core/>

QEMU Emulation Targets
======================

To simplify development, the build system supports building images to
work with the QEMU emulator in system emulation mode and these machines are
the ones included in OE-Core. Our support and testing is currently focused
around the following primary architectures and machine targets:

* ARM (qemuarm + qemuarm64)
* x86 (qemux86 + qemux86-64)
* RISC-V (qemuriscv64)

Use of the QEMU images is covered in the Yocto Project Reference Manual.
The appropriate MACHINE variable value corresponding to the target is given
in brackets.

OpenEmbedded does also have support for PowerPC (qemuppc/qemuppc64), MIPS
(qemumips + qemumips64), qemuriscv32 and other less common targets but these
are not as widely tested.
