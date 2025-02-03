# No default! Either this or IMA_EVM_PRIVKEY/IMA_EVM_X509 have to be
# set explicitly in a local.conf before activating ima-evm-rootfs.
# To use the insecure (because public) example keys, use
# IMA_EVM_KEY_DIR = "${INTEGRITY_BASE}/data/debug-keys"
IMA_EVM_KEY_DIR ?= "IMA_EVM_KEY_DIR_NOT_SET"

# Private key for IMA signing. The default is okay when
# using the example key directory.
IMA_EVM_PRIVKEY ?= "${IMA_EVM_KEY_DIR}/privkey_ima.pem"

# Additional option when signing. Allows to for example provide
# --keyid <id> or --keyid-from-cert <filename>.
IMA_EVM_PRIVKEY_KEYID_OPT ?= ""

# Password for the private key
IMA_EVM_EVMCTL_KEY_PASSWORD ?= ""

# Public part of certificates (used for both IMA and EVM).
# The default is okay when using the example key directory.
IMA_EVM_X509 ?= "${IMA_EVM_KEY_DIR}/x509_ima.der"

# Root CA to be compiled into the kernel, none by default.
# Must be the absolute path to a der-encoded x509 CA certificate
# with a .x509 suffix. See linux-%.bbappend for details.
#
# ima-local-ca.x509 is what ima-gen-local-ca.sh creates.
IMA_EVM_ROOT_CA ?= "${IMA_EVM_KEY_DIR}/ima-local-ca.pem"

# Mount these file systems (identified via their mount point) with
# the iversion flags (needed by IMA when allowing writing).
IMA_EVM_ROOTFS_IVERSION ?= ""

# Avoid re-generating fstab when ima is enabled.
WIC_CREATE_EXTRA_ARGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', ' --no-fstab-update', '', d)}"

# Add necessary tools (e.g., keyctl) to image
IMAGE_INSTALL:append = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', ' ima-evm-utils', '', d)}"

ima_evm_sign_rootfs () {
    cd ${IMAGE_ROOTFS}

    # Beware that all operations below must also work when
    # ima_evm_sign_rootfs was already called earlier for the same
    # rootfs. That's because do_image might again run for various
    # reasons (including a change of the signing keys) without also
    # re-running do_rootfs.

    # Fix /etc/fstab: it must include the "i_version" mount option for
    # those file systems where writing files is allowed, otherwise
    # these changes will not get detected at runtime.
    #
    # Note that "i_version" is documented in "man mount" only for ext4,
    # whereas "iversion" is said to be filesystem-independent. In practice,
    # there is only one MS_I_VERSION flag in the syscall and ext2/ext3/ext4
    # all support it.
    #
    # coreutils translates "iversion" into MS_I_VERSION. busybox rejects
    # "iversion" and only understands "i_version". systemd only understands
    # "iversion". We pick "iversion" here for systemd, whereas rootflags
    # for initramfs must use "i_version" for busybox.
    #
    # Deduplicates iversion in case that this gets called more than once.
    if [ -f etc/fstab ]; then
       perl -pi -e 's;(\S+)(\s+)(${@"|".join((d.getVar("IMA_EVM_ROOTFS_IVERSION") or "no-such-mount-point").split())})(\s+)(\S+)(\s+)(\S+);\1\2\3\4\5\6\7,iversion;; s/(,iversion)+/,iversion/;' etc/fstab
    fi

    # Detect 32bit target to pass --m32 to evmctl by looking at libc
    tmp="$(file "${IMAGE_ROOTFS}/lib/libc.so.6" | grep -o 'ELF .*-bit')"
    if [ "${tmp}" = "ELF 32-bit" ]; then
        evmctl_param="--m32"
    elif [ "${tmp}" = "ELF 64-bit" ]; then
        evmctl_param=""
    else
        bberror "Unknown target architecture bitness: '${tmp}'" >&2
        exit 1
    fi

    export EVMCTL_KEY_PASSWORD=${IMA_EVM_EVMCTL_KEY_PASSWORD}

    bbnote "IMA/EVM: Signing root filesystem at ${IMAGE_ROOTFS} with key ${IMA_EVM_PRIVKEY}"
    evmctl sign --imasig ${evmctl_param} --portable -a sha256 \
        --key "${IMA_EVM_PRIVKEY}" ${IMA_EVM_PRIVKEY_KEYID_OPT} -r "${IMAGE_ROOTFS}"

    # check signing key and signature verification key
    evmctl ima_verify ${evmctl_param} --key "${IMA_EVM_X509}" "${IMAGE_ROOTFS}/lib/libc.so.6" || exit 1
    evmctl verify     ${evmctl_param} --key "${IMA_EVM_X509}" "${IMAGE_ROOTFS}/lib/libc.so.6" || exit 1

    # Optionally install custom policy for loading by systemd.
    if [ "${IMA_EVM_POLICY}" ]; then
        install -d ./${sysconfdir}/ima
        rm -f ./${sysconfdir}/ima/ima-policy
        install "${IMA_EVM_POLICY}" ./${sysconfdir}/ima/ima-policy

        bbnote "IMA/EVM: Signing IMA policy with key ${IMA_EVM_PRIVKEY}"
        evmctl sign --imasig ${evmctl_param} --portable -a sha256 \
          --key "${IMA_EVM_PRIVKEY}" ${IMA_EVM_PRIVKEY_KEYID_OPT} "${IMAGE_ROOTFS}/etc/ima/ima-policy"
    fi

    # Optionally write the file names and ima and evm signatures into files
    if [ "${IMA_FILE_SIGNATURES_FILE}" ]; then
        getfattr -R -m security.ima --e hex --dump ./ 2>/dev/null | \
          sed -n -e 's|# file: |/|p' -e 's|security.ima=|ima:|p' | \
          sed '$!N;s/\n/ /' > ./${IMA_FILE_SIGNATURES_FILE}
    fi
    if [ "${EVM_FILE_SIGNATURES_FILE}" ]; then
        getfattr -R -m security.evm --e hex --dump ./ 2>/dev/null | \
          sed -n -e 's|# file: |/|p' -e 's|security.evm=|evm:|p' | \
          sed '$!N;s/\n/ /' > ./${EVM_FILE_SIGNATURES_FILE}
    fi
}

# Signing must run as late as possible in the do_rootfs task.
# To guarantee that, we append it to IMAGE_PREPROCESS_COMMAND in
# RecipePreFinalise event handler, this ensures it's the last
# function in IMAGE_PREPROCESS_COMMAND.
python ima_evm_sign_handler () {
    if not e.data or 'ima' not in e.data.getVar('DISTRO_FEATURES').split():
        return

    e.data.appendVar('IMAGE_PREPROCESS_COMMAND', ' ima_evm_sign_rootfs; ')
    e.data.appendVar('IMAGE_INSTALL', ' ima-evm-keys')
    e.data.appendVarFlag('do_rootfs', 'depends', ' ima-evm-utils-native:do_populate_sysroot')
}
addhandler ima_evm_sign_handler
ima_evm_sign_handler[eventmask] = "bb.event.RecipePreFinalise"
