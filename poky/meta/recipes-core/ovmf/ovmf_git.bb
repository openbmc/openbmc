SUMMARY = "OVMF - UEFI firmware for Qemu and KVM"
DESCRIPTION = "OVMF is an EDK II based project to enable UEFI support for \
Virtual Machines. OVMF contains sample UEFI firmware for QEMU and KVM"
HOMEPAGE = "https://github.com/tianocore/tianocore.github.io/wiki/OVMF"
LICENSE = "BSD-2-Clause"
LICENSE_class-target = "${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'BSD & OpenSSL', 'BSD', d)}"
LIC_FILES_CHKSUM = "file://OvmfPkg/License.txt;md5=06357ddc23f46577c2aeaeaf7b776d65"

# Enabling Secure Boot adds a dependency on OpenSSL and implies
# compiling OVMF twice, so it is disabled by default. Distros
# may change that default.
PACKAGECONFIG ??= ""
PACKAGECONFIG[secureboot] = ",,,"

SRC_URI = "gitsm://github.com/tianocore/edk2.git;branch=master;protocol=git \
           file://0001-ovmf-update-path-to-native-BaseTools.patch \
           file://0002-BaseTools-makefile-adjust-to-build-in-under-bitbake.patch \
           file://0003-ovmf-enable-long-path-file.patch \
           file://0004-ovmf-Update-to-latest.patch \
        "

PV = "edk2-stable201911"
SRCREV = "bd85bf54c268204c7a698a96f3ccd96cd77952cd"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>edk2-stable.*)"

inherit deploy

PARALLEL_MAKE = ""

S = "${WORKDIR}/git"

DEPENDS = "nasm-native acpica-native ovmf-native util-linux-native"

EDK_TOOLS_DIR="edk2_basetools"

# OVMF has trouble building with the default optimization of -O2.
BUILD_OPTIMIZATION="-pipe"

# OVMF supports IA only, although it could conceivably support ARM someday.
COMPATIBLE_HOST='(i.86|x86_64).*'

# Additional build flags for OVMF with Secure Boot.
# Fedora also uses "-D SMM_REQUIRE -D EXCLUDE_SHELL_FROM_FD".
OVMF_SECURE_BOOT_EXTRA_FLAGS ??= ""
OVMF_SECURE_BOOT_FLAGS = "-DSECURE_BOOT_ENABLE=TRUE ${OVMF_SECURE_BOOT_EXTRA_FLAGS}"

export PYTHON_COMMAND = "${HOSTTOOLS_DIR}/python3"

do_patch[postfuncs] += "fix_basetools_location"
fix_basetools_location () {
}
fix_basetools_location_class-target() {
    # Replaces the fake path inserted by 0002-ovmf-update-path-to-native-BaseTools.patch.
    # Necessary for finding the actual BaseTools from ovmf-native.
    sed -i -e 's#BBAKE_EDK_TOOLS_PATH#${STAGING_BINDIR_NATIVE}/${EDK_TOOLS_DIR}#' ${S}/OvmfPkg/build.sh
}

do_patch[postfuncs] += "fix_iasl"
fix_iasl() {
}
fix_iasl_class-native() {
    # iasl is not installed under /usr/bin when building with OE.
    sed -i -e 's#/usr/bin/iasl#${STAGING_BINDIR_NATIVE}/iasl#' ${S}/BaseTools/Conf/tools_def.template
}

# Inject CC and friends into the build. LINKER already is in GNUmakefile.
# Must be idempotent and thus remove old assignments that were inserted
# earlier.
do_patch[postfuncs] += "fix_toolchain"
fix_toolchain() {
    sed -i \
        -e '/^\(CC\|CXX\|AS\|AR\|LD\|LINKER\) =/d' \
        -e '/^APPLICATION/a CC = ${CC}\nCXX = ${CXX}\nAS = ${AS}\nAR = ${AR}\nLD = ${LD}\nLINKER = $(CC)' \
        ${S}/BaseTools/Source/C/Makefiles/app.makefile
    sed -i \
        -e '/^\(CC\|CXX\|AS\|AR\|LD\)/d' \
        -e '/^VFR_CPPFLAGS/a CC = ${CC}\nCXX = ${CXX}\nAS = ${AS}\nAR = ${AR}\nLD = ${LD}' \
        ${S}/BaseTools/Source/C/VfrCompile/GNUmakefile
}
fix_toolchain_append_class-native() {
    # This tools_def.template is going to be used by the target ovmf and
    # defines which compilers to use. For the GCC toolchain definitions,
    # that will be ${HOST_PREFIX}gcc. However, "make" doesn't need that
    # prefix.
    #
    # Injecting ENV(HOST_PREFIX) matches exporting that value as env
    # variable in do_compile_class-target.
    sed -i \
        -e 's#\(ENV\|DEF\)(GCC.*_PREFIX)#ENV(HOST_PREFIX)#' \
        -e 's#ENV(HOST_PREFIX)make#make#' \
        ${S}/BaseTools/Conf/tools_def.template
    sed -i \
        -e '/^\(LFLAGS\|CFLAGS\) +=/d' \
        -e '/^LINKER/a LFLAGS += ${BUILD_LDFLAGS}\nCFLAGS += ${BUILD_CFLAGS}' \
        ${S}/BaseTools/Source/C/Makefiles/app.makefile \
        ${S}/BaseTools/Source/C/VfrCompile/GNUmakefile
    # Linking with gold fails:
    # internal error in do_layout, at ../../gold/object.cc:1821
    # make: *** [.../OUTPUT/Facs.acpi] Error 1
    # We intentionally hard-code the use of ld.bfd regardless of DISTRO_FEATURES
    # to make ovmf-native reusable across distros.
    sed -i \
        -e 's#^\(DEFINE GCC.*DLINK.*FLAGS  *=\)#\1 -fuse-ld=bfd#' \
        ${S}/BaseTools/Conf/tools_def.template
}

GCC_VER="$(${CC} -v 2>&1 | tail -n1 | awk '{print $3}')"

fixup_target_tools() {
    case ${1} in
      4.4.*)
        FIXED_GCCVER=GCC44
        ;;
      4.5.*)
        FIXED_GCCVER=GCC45
        ;;
      4.6.*)
        FIXED_GCCVER=GCC46
        ;;
      4.7.*)
        FIXED_GCCVER=GCC47
        ;;
      4.8.*)
        FIXED_GCCVER=GCC48
        ;;
      4.9.*)
        FIXED_GCCVER=GCC49
        ;;
      *)
        FIXED_GCCVER=GCC5
        ;;
    esac
    echo ${FIXED_GCCVER}
}

do_compile_class-native() {
    oe_runmake -C ${S}/BaseTools
}

do_compile_class-target() {
    export LFLAGS="${LDFLAGS}"
    PARALLEL_JOBS="${@oe.utils.parallel_make_argument(d, '-n %d')}"
    OVMF_ARCH="X64"
    if [ "${TARGET_ARCH}" != "x86_64" ] ; then
        OVMF_ARCH="IA32"
    fi

    # The build for the target uses BaseTools/Conf/tools_def.template
    # from ovmf-native to find the compiler, which depends on
    # exporting HOST_PREFIX.
    export HOST_PREFIX="${HOST_PREFIX}"

    # BaseTools/Conf gets copied to Conf, but only if that does not
    # exist yet. To ensure that an updated template gets used during
    # incremental builds, we need to remove the copy before we start.
    rm -f `ls ${S}/Conf/*.txt | grep -v ReadMe.txt`

    # ${WORKDIR}/ovmf is a well-known location where do_install and
    # do_deploy will be able to find the files.
    rm -rf ${WORKDIR}/ovmf
    mkdir ${WORKDIR}/ovmf
    OVMF_DIR_SUFFIX="X64"
    if [ "${TARGET_ARCH}" != "x86_64" ] ; then
        OVMF_DIR_SUFFIX="Ia32" # Note the different capitalization
    fi
    FIXED_GCCVER=$(fixup_target_tools ${GCC_VER})
    bbnote FIXED_GCCVER is ${FIXED_GCCVER}
    build_dir="${S}/Build/Ovmf$OVMF_DIR_SUFFIX/RELEASE_${FIXED_GCCVER}"

    bbnote "Building without Secure Boot."
    rm -rf ${S}/Build/Ovmf$OVMF_DIR_SUFFIX
    ${S}/OvmfPkg/build.sh $PARALLEL_JOBS -a $OVMF_ARCH -b RELEASE -t ${FIXED_GCCVER}
    ln ${build_dir}/FV/OVMF.fd ${WORKDIR}/ovmf/ovmf.fd
    ln ${build_dir}/FV/OVMF_CODE.fd ${WORKDIR}/ovmf/ovmf.code.fd
    ln ${build_dir}/FV/OVMF_VARS.fd ${WORKDIR}/ovmf/ovmf.vars.fd
    ln ${build_dir}/${OVMF_ARCH}/Shell.efi ${WORKDIR}/ovmf/

    if ${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'true', 'false', d)}; then
        # Repeat build with the Secure Boot flags.
        bbnote "Building with Secure Boot."
        rm -rf ${S}/Build/Ovmf$OVMF_DIR_SUFFIX
        ${S}/OvmfPkg/build.sh $PARALLEL_JOBS -a $OVMF_ARCH -b RELEASE -t ${FIXED_GCCVER} ${OVMF_SECURE_BOOT_FLAGS}
        ln ${build_dir}/FV/OVMF.fd ${WORKDIR}/ovmf/ovmf.secboot.fd
        ln ${build_dir}/FV/OVMF_CODE.fd ${WORKDIR}/ovmf/ovmf.secboot.code.fd
        ln ${build_dir}/${OVMF_ARCH}/EnrollDefaultKeys.efi ${WORKDIR}/ovmf/
    fi
}

do_install_class-native() {
    install -d ${D}/${bindir}/edk2_basetools
    cp -r ${S}/BaseTools ${D}/${bindir}/${EDK_TOOLS_DIR}
}

do_install_class-target() {
    # Content for UEFI shell iso. We install the EFI shell as
    # bootx64/ia32.efi because then it can be started even when the
    # firmware itself does not contain it.
    install -d ${D}/efi/boot
    install ${WORKDIR}/ovmf/Shell.efi ${D}/efi/boot/boot${@ "ia32" if "${TARGET_ARCH}" != "x86_64" else "x64"}.efi
    if ${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'true', 'false', d)}; then
        install ${WORKDIR}/ovmf/EnrollDefaultKeys.efi ${D}
    fi
}

# This always gets packaged because ovmf-shell-image depends on it.
# This allows testing that recipe in all configurations because it
# can always be part of a world build.
#
# However, EnrollDefaultKeys.efi is only included when Secure Boot is enabled.
PACKAGES =+ "ovmf-shell-efi"
FILES_ovmf-shell-efi = " \
    EnrollDefaultKeys.efi \
    efi/ \
"

DEPLOYDEP = ""
DEPLOYDEP_class-target = "qemu-system-native:do_populate_sysroot"
DEPLOYDEP_class-target += " ${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'openssl-native:do_populate_sysroot', '', d)}"
do_deploy[depends] += "${DEPLOYDEP}"

do_deploy() {
}
do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy_class-target() {
    # For use with "runqemu ovmf".
    for i in \
        ovmf \
        ovmf.code \
        ovmf.vars \
        ${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'ovmf.secboot ovmf.secboot.code', '', d)} \
        ; do
        qemu-img convert -f raw -O qcow2 ${WORKDIR}/ovmf/$i.fd ${DEPLOYDIR}/$i.qcow2
    done

    if ${@bb.utils.contains('PACKAGECONFIG', 'secureboot', 'true', 'false', d)}; then
        # Create a test Platform Key and first Key Exchange Key to use with EnrollDefaultKeys
        openssl req -new -x509 -newkey rsa:2048 -keyout ${DEPLOYDIR}/OvmfPkKek1.key \
                -out ${DEPLOYDIR}/OvmfPkKek1.crt -nodes -days 20 -subj "/CN=OVMFSecBootTest"
        openssl x509 -in ${DEPLOYDIR}/OvmfPkKek1.crt -out ${DEPLOYDIR}/OvmfPkKek1.pem -outform PEM
    fi
}
addtask do_deploy after do_compile before do_build

BBCLASSEXTEND = "native"
TOOLCHAIN = "gcc"
