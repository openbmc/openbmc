# Handle U-Boot config for a machine
#
# The format to specify it, in the machine, is:
#
# UBOOT_MACHINE = "config"
#
# or to build u-boot multiple times with different configs/options:
#
# UBOOT_MACHINE = ""
# UBOOT_CONFIG ??= <default>
# UBOOT_CONFIG[foo] = "foo_config"
# UBOOT_CONFIG[bar] = "bar_config"
#
# UBOOT_CONFIG_IMAGE_FSTYPES[bar] = "fstype"
#
# UBOOT_CONFIG_BINARY[foo] = "binary"
#
# UBOOT_CONFIG_MAKE_OPTS[foo] = "FOO=1"
# UBOOT_CONFIG_MAKE_OPTS[bar] = "BAR=1"
#
# UBOOT_CONFIG_FRAGMENTS[foo] = "foo.fragment"
#
# For more information about this, please see the official documentation.
#
# There is a legacy method that is still supported where some of the above
# controls can be specified in a comma-separated list.  This method was
# deemed to be limiting in terms of expanding support to more and more knobs
# that might need to be turned to customize a config build.
#
# NOTE: Suport for this legacy flow is now deprecated and will be removed from
#       master after the wrynose LTS release.
#
# UBOOT_MACHINE = ""
# UBOOT_CONFIG ??= <default>
# UBOOT_CONFIG[foo] = "foo_config,images,binary"
# UBOOT_CONFIG[bar] = "bar_config,images,binary"
#
# Copyright 2013, 2014 (C) O.S. Systems Software LTDA.
#
# SPDX-License-Identifier: MIT


def removesuffix(s, suffix):
    if suffix and s.endswith(suffix):
        return s[:-len(suffix)]
    return s

UBOOT_ENTRYPOINT ?= "0x20008000"
UBOOT_LOADADDRESS ?= "${UBOOT_ENTRYPOINT}"

# When naming the files we install/deploy, the package version and revision
# are part of the filename.  Create a single variable to represent this and
# allow it to be customized if desired.
UBOOT_VERSION ?= "${PV}-${PR}"

# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
UBOOT_SUFFIX ??= "bin"
UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_BINARYNAME ?= "${@os.path.splitext(d.getVar("UBOOT_BINARY"))[0]}"
UBOOT_IMAGE ?= "${UBOOT_BINARYNAME}-${MACHINE}-${UBOOT_VERSION}.${UBOOT_SUFFIX}"
UBOOT_SYMLINK ?= "${UBOOT_BINARYNAME}-${MACHINE}.${UBOOT_SUFFIX}"
UBOOT_MAKE_TARGET ?= "all"
UBOOT_MAKE_OPTS ?= ""
UBOOT_FRAGMENTS ?= ""

# Output the ELF generated. Some platforms can use the ELF file and directly
# load it (JTAG booting, QEMU) additionally the ELF can be used for debugging
# purposes.
UBOOT_ELF ?= ""
UBOOT_ELF_SUFFIX ?= "elf"
UBOOT_ELF_IMAGE ?= "u-boot-${MACHINE}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX}"
UBOOT_ELF_BINARY ?= "u-boot.${UBOOT_ELF_SUFFIX}"
UBOOT_ELF_SYMLINK ?= "u-boot-${MACHINE}.${UBOOT_ELF_SUFFIX}"

# Some versions of u-boot build an SPL (Second Program Loader) image that
# should be packaged along with the u-boot binary as well as placed in the
# deploy directory.  For those versions they can set the following variables
# to allow packaging the SPL.
SPL_SUFFIX ?= ""
SPL_BINARY ?= ""
SPL_DELIMITER  ?= "${@'.' if d.getVar("SPL_SUFFIX") else ''}"
SPL_BINARYFILE ?= "${@os.path.basename(d.getVar("SPL_BINARY"))}"
SPL_BINARYNAME ?= "${@removesuffix(d.getVar("SPL_BINARYFILE"), "." + d.getVar("SPL_SUFFIX"))}"
SPL_IMAGE ?= "${SPL_BINARYNAME}-${MACHINE}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX}"
SPL_SYMLINK ?= "${SPL_BINARYNAME}-${MACHINE}${SPL_DELIMITER}${SPL_SUFFIX}"

# Additional environment variables or a script can be installed alongside
# u-boot to be used automatically on boot.  This file, typically 'uEnv.txt'
# or 'boot.scr', should be packaged along with u-boot as well as placed in the
# deploy directory.  Machine configurations needing one of these files should
# include it in the SRC_URI and set the UBOOT_ENV parameter.
UBOOT_ENV_SUFFIX ?= "txt"
UBOOT_ENV ?= ""
UBOOT_ENV_SRC_SUFFIX ?= "cmd"
UBOOT_ENV_SRC ?= "${UBOOT_ENV}.${UBOOT_ENV_SRC_SUFFIX}"
UBOOT_ENV_BINARY ?= "${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_IMAGE ?= "${UBOOT_ENV}-${MACHINE}-${UBOOT_VERSION}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_SYMLINK ?= "${UBOOT_ENV}-${MACHINE}.${UBOOT_ENV_SUFFIX}"

# Enable the build of the U-Boot initial env binary image. The binary size is
# required (i.e. the U-Boot partition environment size). Since the environment
# layout with multiple copies is different, set UBOOT_INITIAL_ENV_BINARY_REDUND
# to "1" if the U-Boot environment redundancy is enabled.
UBOOT_INITIAL_ENV_BINARY ?= "0"
UBOOT_INITIAL_ENV_BINARY_SIZE ?= ""
UBOOT_INITIAL_ENV_BINARY_REDUND ?= "0"

# U-Boot EXTLINUX variables. U-Boot searches for /boot/extlinux/extlinux.conf
# to find EXTLINUX conf file.
UBOOT_EXTLINUX_INSTALL_DIR ?= "/boot/extlinux"
UBOOT_EXTLINUX_CONF_NAME ?= "extlinux.conf"
UBOOT_EXTLINUX_SYMLINK ?= "${UBOOT_EXTLINUX_CONF_NAME}-${MACHINE}-${UBOOT_VERSION}"

# Options for the device tree compiler passed to mkimage '-D' feature:
UBOOT_MKIMAGE_DTCOPTS ??= ""
SPL_MKIMAGE_DTCOPTS ??= ""

# mkimage command
UBOOT_MKIMAGE ?= "uboot-mkimage"
UBOOT_MKIMAGE_SIGN ?= "${UBOOT_MKIMAGE}"

# Signature activation
UBOOT_SIGN_ENABLE ?= "0"

# Arguments passed to mkimage for signing
UBOOT_MKIMAGE_SIGN_ARGS ?= ""
SPL_MKIMAGE_SIGN_ARGS ?= ""

# Options to deploy the u-boot device tree
UBOOT_DTB ?= ""
UBOOT_DTB_BINARY ??= ""

# uboot-fit_check_sign command
UBOOT_FIT_CHECK_SIGN ?= "uboot-fit_check_sign"

python () {
    if bb.utils.to_boolean(d.getVar('UBOOT_INITIAL_ENV_BINARY')) and d.getVar('UBOOT_INITIAL_ENV_BINARY_SIZE') == "":
        bb.fatal("UBOOT_INITIAL_ENV_BINARY requires setting the U-Boot partition environment size with the UBOOT_INITIAL_ENV_BINARY_SIZE variable")

    ubootmachine = d.getVar("UBOOT_MACHINE")
    ubootconfigflags = d.getVarFlags('UBOOT_CONFIG')
    ubootconfigimagefstypes = d.getVar('UBOOT_CONFIG_IMAGE_FSTYPES')
    ubootconfigimagefstypesflags = d.getVarFlags('UBOOT_CONFIG_IMAGE_FSTYPES')
    ubootbinary = d.getVar('UBOOT_BINARY')
    ubootconfigbinary = d.getVar('UBOOT_CONFIG_BINARY')
    ubootconfigbinaryflags = d.getVarFlags('UBOOT_CONFIG_BINARY')
    ubootconfigmakeopts = d.getVar('UBOOT_CONFIG_MAKE_OPTS')
    ubootconfigmakeoptsflags = d.getVarFlags('UBOOT_CONFIG_MAKE_OPTS')
    ubootconfigfragments = d.getVar('UBOOT_CONFIG_FRAGMENTS')
    ubootconfigfragmentsflags = d.getVarFlags('UBOOT_CONFIG_FRAGMENTS')
    # The "doc" varflag is special, we don't want to see it here
    ubootconfigflags.pop('doc', None)
    ubootconfig = (d.getVar('UBOOT_CONFIG') or "").split()
    recipename = d.getVar("PN")

    if not ubootmachine and not ubootconfig:
        FILE = os.path.basename(d.getVar("FILE"))
        bb.debug(1, "To build %s, see %s for instructions on \
                 setting up your machine config" % (recipename, FILE))
        raise bb.parse.SkipRecipe("Either UBOOT_MACHINE or UBOOT_CONFIG must be set in the %s machine configuration." % d.getVar("MACHINE"))

    if ubootmachine and ubootconfig:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_MACHINE and UBOOT_CONFIG at the same time.")

    if ubootconfigimagefstypes:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_CONFIG_IMAGE_FSTYPES as a variable, you can only set flags.")

    if ubootconfigbinary:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_CONFIG_BINARY as a variable, you can only set flags.")

    if ubootconfigmakeopts:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_CONFIG_MAKE_OPTS as a variable, you can only set flags.")

    if ubootconfigfragments:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_CONFIG_FRAGMENTS as a variable, you can only set flags.")

    if len(ubootconfig) > 0:
        for config in ubootconfig:
            found = False
            binary = ubootbinary
            imagefstype = ""
            for f, v in ubootconfigflags.items():
                if config == f: 
                    found = True
                    items = v.split(',')
                    if items[0] and len(items) > 1:
                        bb.warn('Legacy use of UBOOT_CONFIG[%s] = "%s" is deprecated.  Please move to using UBOOT_CONFIG_* variables:' % (f, v))
                        bb.warn('    UBOOT_CONFIG[%s] = "%s"' % (f, items[0]))
                    if items[0] and len(items) > 3:
                        raise bb.parse.SkipRecipe('Only config,images,binary can be specified!')
                    d.appendVar('UBOOT_MACHINE', ' ' + items[0])
                    # IMAGE_FSTYPES appending
                    if len(items) > 1 and items[1]:
                        bb.warn('    UBOOT_CONFIG_IMAGE_FSTYPES[%s] = "%s"' % (f, items[1]))
                        bb.debug(1, "Staging '%s' for IMAGE_FSTYPES." % items[1])
                        imagefstype = items[1]
                    if len(items) > 2 and items[2]:
                        bb.warn('    UBOOT_CONFIG_BINARY[%s] = "%s"' % (f, items[2]))
                        bb.debug(1, "Staging '%s' for UBOOT_CONFIG_BINARY." % items[2])
                        binary = items[2]
                    break

            if not found:
                raise bb.parse.SkipRecipe("The selected UBOOT_CONFIG key %s has no match in %s." % (config, ubootconfigflags.keys()))

            # Extract out any settings from UBOOT_IMAGE_FSTYPES[config]
            if  ubootconfigimagefstypesflags:
                for f, v in ubootconfigimagefstypesflags.items():
                    if config == f:
                        bb.debug(1, "Staging '%s' for IMAGE_FSTYPES." % v)
                        imagefstype = v

            if imagefstype:
                bb.debug(1, "Appending '%s' to IMAGE_FSTYPES." % imagefstype)
                d.appendVar('IMAGE_FSTYPES', ' ' + imagefstype)

            # Extract out any settings from UBOOT_CONFIG_BINARY[config]
            if  ubootconfigbinaryflags:
                for f, v in ubootconfigbinaryflags.items():
                    if config == f:
                        bb.debug(1, "Staging '%s' for UBOOT_CONFIG_BINARY." % v)
                        binary = v

            bb.debug(1, "Appending '%s' to UBOOT_CONFIG_BINARY." % binary)
            d.appendVar('UBOOT_CONFIG_BINARY', binary + " ? ")

            # Extract out any settings from UBOOT_CONFIG_MAKE_OPTS[config]
            make_opts = ""
            if  ubootconfigmakeoptsflags:
                for f, v in ubootconfigmakeoptsflags.items():
                    if config == f:
                        bb.debug(1, "Staging '%s' for UBOOT_CONFIG_MAKE_OPTS." % v)
                        make_opts = v

            bb.debug(1, "Appending '%s' to UBOOT_CONFIG_MAKE_OPTS." % make_opts)
            d.appendVar('UBOOT_CONFIG_MAKE_OPTS', make_opts + " ? ")

            # Extract out any settings from UBOOT_CONFIG_FRAGMENTS[config]
            fragments = ""
            if ubootconfigfragmentsflags:
                for f, v in ubootconfigfragmentsflags.items():
                    if config == f:
                        bb.debug(1, "Staging '%s' for UBOOT_CONFIG_FRAGMENTS." % v)
                        fragments = v

            bb.debug(1, "Appending '%s' to UBOOT_CONFIG_FRAGMENTS." % fragments)
            d.appendVar('UBOOT_CONFIG_FRAGMENTS', fragments + " ? ")

            # This recipe might be inherited e.g. by the kernel recipe via kernel-fitimage.bbclass
            # Ensure the uboot specific menuconfig settings do not leak into other recipes
            if 'u-boot' in recipename:
                if len(ubootconfig) == 1:
                    builddir = "%s-%s" % (d.getVar("UBOOT_MACHINE").strip(), config)
                    d.setVar('KCONFIG_CONFIG_ROOTDIR', os.path.join("${B}", builddir))
                else:
                    # Disable menuconfig for multiple configs
                    d.setVar('KCONFIG_CONFIG_ENABLE_MENUCONFIG', "false")
}

uboot_config_get_indexed_value () {
    local list=$1
    local index=$2

    local k=""

    IFS="?"
    for value in $list; do
        k=$(expr $k + 1);
        if [ $k -eq $index ]; then
            break
        fi
    done
    unset IFS

    echo "$value"
}
