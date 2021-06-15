Poky
====

Poky is an integration of various components to form a complete prepackaged
build system and development environment. It features support for building
customised embedded device style images. There are reference demo images
featuring a X11/Matchbox/GTK themed UI called Sato. The system supports
cross-architecture application development using QEMU emulation and a
standalone toolchain and SDK with IDE integration.

Additional information on the specifics of hardware that Poky supports
is available in README.hardware. Further hardware support can easily be added
in the form of layers which extend the systems capabilities in a modular way.

As an integration layer Poky consists of several upstream projects such as 
BitBake, OpenEmbedded-Core, Yocto documentation and various sources of information 
e.g. for the hardware support. Poky is in turn a component of the Yocto Project.

The Yocto Project has extensive documentation about the system including a 
reference manual which can be found at:
    http://yoctoproject.org/documentation

OpenEmbedded-Core is a layer containing the core metadata for current versions
of OpenEmbedded. It is distro-less (can build a functional image with
DISTRO = "nodistro") and contains only emulated machine support.

For information about OpenEmbedded, see the OpenEmbedded website:
    http://www.openembedded.org/


Contribution Guidelines
=======================

A guide to submitting patches to OpenEmbedded is available at:

http://www.openembedded.org/wiki/How_to_submit_a_patch_to_OpenEmbedded

There is good documentation on how to write/format patches at:

https://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines


Where to Send Patches
=====================

As Poky is an integration repository (built using a tool called combo-layer),
patches against the various components should be sent to their respective
upstreams:

bitbake:
    Git repository: http://git.openembedded.org/bitbake/
    Mailing list: bitbake-devel@lists.openembedded.org

documentation:
    Git repository: http://git.yoctoproject.org/cgit/cgit.cgi/yocto-docs/
    Mailing list: docs@lists.yoctoproject.org

meta-poky, meta-yocto-bsp:
    Git repository: http://git.yoctoproject.org/cgit/cgit.cgi/meta-yocto(-bsp)
    Mailing list: poky@lists.yoctoproject.org

Everything else should be sent to the OpenEmbedded Core mailing list.  If in
doubt, check the oe-core git repository for the content you intend to modify.
Before sending, be sure the patches apply cleanly to the current oe-core git
repository.

    Git repository: http://git.openembedded.org/openembedded-core/
    Mailing list: openembedded-core@lists.openembedded.org

Note: The scripts directory should be treated with extra care as it is a mix of
oe-core and poky-specific files from meta-poky.
