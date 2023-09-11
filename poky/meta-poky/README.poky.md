Poky
====

Poky is an integration of various components to form a pre-packaged
build system and development environment which is used as a development and
validation tool by the [Yocto Project](https://www.yoctoproject.org/). It
features support for building customised embedded style device images
and custom containers. There are reference demo images ranging from X11/GTK+
 to Weston, commandline and more. The system supports cross-architecture
application development using QEMU emulation and a standalone toolchain and
SDK suitable for IDE integration.

Additional information on the specifics of hardware that Poky supports
is available in README.hardware. Further hardware support can easily be added
in the form of BSP layers which extend the systems capabilities in a modular way.
Many layers are available and can be found through the
[layer index](https://layers.openembedded.org/).

As an integration layer Poky consists of several upstream projects such as 
[BitBake](https://git.openembedded.org/bitbake/),
[OpenEmbedded-Core](https://git.openembedded.org/openembedded-core/),
[Yocto documentation](https://git.yoctoproject.org/cgit.cgi/yocto-docs/),
the '[meta-yocto](https://git.yoctoproject.org/cgit.cgi/meta-yocto/)' layer
which has configuration and hardware support components. These components
are all part of the Yocto Project and OpenEmbedded ecosystems.

The Yocto Project has extensive documentation about the system including a 
reference manual which can be found at <https://docs.yoctoproject.org/>

OpenEmbedded is the build architecture used by Poky and the Yocto project.
For information about OpenEmbedded, see the 
[OpenEmbedded website](https://www.openembedded.org/).

Contribution Guidelines
-----------------------

Please refer to our contributor guide here: https://docs.yoctoproject.org/dev/contributor-guide/
for full details on how to submit changes.

Where to Send Patches
---------------------

As Poky is an integration repository (built using a tool called combo-layer),
patches against the various components should be sent to their respective
upstreams:

OpenEmbedded-Core (files in meta/, meta-selftest/, meta-skeleton/, scripts/):

- Git repository: <https://git.openembedded.org/openembedded-core/>
- Mailing list: openembedded-core@lists.openembedded.org

BitBake (files in bitbake/):

- Git repository: <https://git.openembedded.org/bitbake/>
- Mailing list: bitbake-devel@lists.openembedded.org

Documentation (files in documentation/):

- Git repository: <https://git.yoctoproject.org/cgit/cgit.cgi/yocto-docs/>
- Mailing list: docs@lists.yoctoproject.org

meta-yocto (files in meta-poky/, meta-yocto-bsp/):

- Git repository: <https://git.yoctoproject.org/cgit/cgit.cgi/meta-yocto>
- Mailing list: poky@lists.yoctoproject.org

If in doubt, check the openembedded-core git repository for the content you
intend to modify as most files are from there unless clearly one of the above
categories. Before sending, be sure the patches apply cleanly to the current
git repository branch in question.

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/765/badge)](https://bestpractices.coreinfrastructure.org/projects/765)

