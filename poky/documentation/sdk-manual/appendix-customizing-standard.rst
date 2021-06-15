.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

****************************
Customizing the Standard SDK
****************************

This appendix presents customizations you can apply to the standard SDK.

Adding Individual Packages to the Standard SDK
==============================================

When you build a standard SDK using the ``bitbake -c populate_sdk``, a
default set of packages is included in the resulting SDK. The
:term:`TOOLCHAIN_HOST_TASK`
and
:term:`TOOLCHAIN_TARGET_TASK`
variables control the set of packages adding to the SDK.

If you want to add individual packages to the toolchain that runs on the
host, simply add those packages to the ``TOOLCHAIN_HOST_TASK`` variable.
Similarly, if you want to add packages to the default set that is part
of the toolchain that runs on the target, add the packages to the
``TOOLCHAIN_TARGET_TASK`` variable.

Adding API Documentation to the Standard SDK
============================================

You can include API documentation as well as any other documentation
provided by recipes with the standard SDK by adding "api-documentation"
to the
:term:`DISTRO_FEATURES`
variable: DISTRO_FEATURES_append = " api-documentation" Setting this
variable as shown here causes the OpenEmbedded build system to build the
documentation and then include it in the standard SDK.
