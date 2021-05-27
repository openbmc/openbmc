# No default! Either this or IMA_EVM_PRIVKEY/IMA_EVM_X509 have to be
# set explicitly in a local.conf before activating ima-evm-rootfs.
# To use the insecure (because public) example keys, use
# IMA_EVM_KEY_DIR = "${INTEGRITY_BASE}/data/debug-keys"
IMA_EVM_KEY_DIR ?= "IMA_EVM_KEY_DIR_NOT_SET"

# Private key for IMA signing. The default is okay when
# using the example key directory.
IMA_EVM_PRIVKEY ?= "${IMA_EVM_KEY_DIR}/privkey_ima.pem"

# Public part of certificates (used for both IMA and EVM).
# The default is okay when using the example key directory.
IMA_EVM_X509 ?= "${IMA_EVM_KEY_DIR}/x509_ima.der"

# Root CA to be compiled into the kernel, none by default.
# Must be the absolute path to a der-encoded x509 CA certificate
# with a .x509 suffix. See linux-%.bbappend for details.
#
# ima-local-ca.x509 is what ima-gen-local-ca.sh creates.
IMA_EVM_ROOT_CA ?= ""

# Sign all regular files by default.
IMA_EVM_ROOTFS_SIGNED ?= ". -type f"
# Hash nothing by default.
IMA_EVM_ROOTFS_HASHED ?= ". -depth 0 -false"

# Mount these file systems (identified via their mount point) with
# the iversion flags (needed by IMA when allowing writing).
IMA_EVM_ROOTFS_IVERSION ?= ""

# Avoid re-generating fstab when ima is enabled.
WIC_CREATE_EXTRA_ARGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', ' --no-fstab-update', '', d)}"

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
       perl -pi -e 's;(\S+)(\s+)(${@"|".join((d.getVar("IMA_EVM_ROOTFS_IVERSION", True) or "no-such-mount-point").split())})(\s+)(\S+)(\s+)(\S+);\1\2\3\4\5\6\7,iversion;; s/(,iversion)+/,iversion/;' etc/fstab
    fi

    # Sign file with private IMA key. EVM not supported at the moment.
    bbnote "IMA/EVM: signing files 'find ${IMA_EVM_ROOTFS_SIGNED}' with private key '${IMA_EVM_PRIVKEY}'"
    find ${IMA_EVM_ROOTFS_SIGNED} | xargs -d "\n" --no-run-if-empty --verbose evmctl ima_sign --key ${IMA_EVM_PRIVKEY}
    bbnote "IMA/EVM: hashing files 'find ${IMA_EVM_ROOTFS_HASHED}'"
    find ${IMA_EVM_ROOTFS_HASHED} | xargs -d "\n" --no-run-if-empty --verbose evmctl ima_hash

    # Optionally install custom policy for loading by systemd.
    if [ "${IMA_EVM_POLICY_SYSTEMD}" ]; then
        install -d ./${sysconfdir}/ima
        rm -f ./${sysconfdir}/ima/ima-policy
        install "${IMA_EVM_POLICY_SYSTEMD}" ./${sysconfdir}/ima/ima-policy
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
