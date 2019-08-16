This README file contains information on the contents of the
integrity layer.

Please see the corresponding sections below for details.


Dependencies
============

This layer depends on:

    URI: git://git.openembedded.org/bitbake
    branch: master

    URI: git://git.openembedded.org/openembedded-core
    layers: meta
    branch: master

    URI: git://github.com/01org/meta-security/meta-integrate
    layers: security-framework
    branch: master


Patches
=======

For discussion or patch submission via email, use the
yocto@yoctoproject.org mailing list. When submitting patches that way,
make sure to copy the maintainer and add a "[meta-integrity]"
prefix to the subject of the mails.

Maintainer: Armin Kuster <akuster808@gmail.com>


Table of Contents
=================

1. Adding the integrity layer to your build
2. Usage
3. Known Issues


1. Adding the integrity layer to your build
===========================================

In order to use this layer, you need to make the build system aware of
it.

Assuming the security repository exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the integrity layer to bblayers.conf, along with any
other layers needed. e.g.:

    BBLAYERS ?= " \
      /path/to/yocto/meta \
      /path/to/yocto/meta-yocto \
      /path/to/yocto/meta-yocto-bsp \
      /path/to/yocto/meta-security/meta-integrity \
      "

It has some dependencies on a suitable BSP; in particular the kernel
must have a recent enough IMA/EVM subsystem. The layer was tested with
Linux 3.19 and uses some features (like loading X509 certificates
directly from the kernel) which were added in that release. Your
mileage may vary with older kernels.

The necessary kernel configuration parameters are added to all kernel
versions by this layer. Watch out for QA warnings about unused kernel
configuration parameters: those indicate that the kernel used by the BSP
does not have the necessary IMA/EVM features.

Adding the layer only enables IMA (see below regarding EVM) during
compilation of the Linux kernel. To also activate it when building
the image, enable image signing in the local.conf like this:

    INHERIT += "ima-evm-rootfs"
    IMA_EVM_KEY_DIR = "${INTEGRITY_BASE}/data/debug-keys"

This uses the default keys provided in the "data" directory of the layer.
Because everyone has access to these private keys, such an image
should never be used in production!

For that, create your own keys first. All tools and scripts required
for that are included in the layer. This is also how the
``debug-keys`` were generated:

    # Choose a directory for storing keys. Preserve this
    # across builds and keep its private keys secret!
    export IMA_EVM_KEY_DIR=/tmp/imaevm
    mkdir -p $IMA_EVM_KEY_DIR
    # Build the required tools.
    bitbake openssl-native
    # Set up shell for use of the tools.
    bitbake -c devshell openssl-native
    cd $IMA_EVM_KEY_DIR
    # In that shell, create the keys. Several options exist:

    # 1. Self-signed keys.
    $INTEGRITY_BASE/scripts/ima-gen-self-signed.sh

    # 2. Keys signed by a new CA.
    # When asked for a PEM passphrase, that will be for the root CA.
    # Signing images then will not require entering that passphrase,
    # only creating new certificates does. Most likely the default
    # attributes for these certificates need to be adapted; modify
    # the scripts as needed.
    # $INTEGRITY_BASE/scripts/ima-gen-local-ca.sh
    # $INTEGRITY_BASE/scripts/ima-gen-CA-signed.sh

    # 3. Keys signed by an existing CA.
    # $INTEGRITY_BASE/scripts/ima-gen-CA-signed.sh <CA.pem> <CA.priv>
    exit

When using ``ima-self-signed.sh`` as described above, self-signed keys
are created. Alternatively, one can also use keys signed by a CA.  The
``ima-gen-local-ca.sh`` and ``ima-gen.sh`` scripts create a root CA
and sign the signing keys with it. The ``ima-evm-rootfs.bbclass`` then
supports adding tha CA's public key to the kernel's system keyring by
compiling it directly into the kernel. Because it is unknown whether
that is necessary (for example, the CA might also get added to the
system key ring via UEFI Secure Boot), one has to enable compilation
into the kernel explicitly in a local.conf with:

    IMA_EVM_ROOT_CA = "<path to .x509 file, for example the ima-local-ca.x509 created by ima-gen-local-ca.sh>"




To use the personal keys, override the default IMA_EVM_KEY_DIR in your
local.conf and/or override the individual variables from
ima-evm-rootfs.bbclass:

    IMA_EVM_KEY_DIR = "<full path>"
    IMA_EVM_PRIVKEY = "<some other path/privkey_ima.pem>"

By default, the entire file system gets signed. When using a policy which
does not require that, the set of files to be labelled can be chosen
by overriding the default "find" expression, for example like this:

    IMA_EVM_ROOTFS_FILES = "usr sbin bin lib -type f"


2. Usage
========

After creating an image with IMA/EVM enabled, one needs to enable
the built-in policies before IMA/EVM is active at runtime. To do this,
add one or both of these boot parameters:

    ima_tcb # measures all files read as root and all files executed
    ima_appraise_tcb # appraises all files owned by root, beware of
                     # the known issue mentioned below

Instead of booting with default policies, one can also activate custom
policies in different ways. First, boot without any IMA policy and
then cat a policy file into
`/sys/kernel/security/ima/policy`. This can only be done once
after booting and is useful for debugging.

In production, the long term goal is to load a verified policy
directly from the kernel, using a patch which still needs to be
included upstream ("ima: load policy from the kernel",
<https://lwn.net/Articles/595759/>).

Loading via systemd also works with systemd, but is considered less
secure (policy file is not checked before activating it). Beware that
IMA policy loading became broken in systemd 2.18. The modified systemd
2.19 in meta-security-smack has a patch reverting the broken
changes. To activate policy loading via systemd, place a policy file
in `/etc/ima/ima-policy`, for example with:

    IMA_EVM_POLICY_SYSTEMD = "${INTEGRITY_BASE}/data/ima_policy_simple"

To check that measuring works, look at `/sys/kernel/security/ima/ascii_runtime_measurements`

To check that appraisal works, try modifying executables and ensure
that executing them fails:

    echo "foobar" >>/usr/bin/rpm
    evmctl ima_verify /usr/bin/rpm
    rpm --version

Depending on the current appraisal policy, the `echo` command may
already fail because writing is not allowed. If the file was modified
and the current appraisal policy allows reading, then `evmctl` will
report (the errno value seems to be printed always and is unrelated to
the actual verification failure here):

    Verification failed: 35
    errno: No such file or directory (2)

After enabling a suitable IMA appraisal policy, reading and/or
executing the file is no longer allowed:

    # evmctl ima_verify /usr/bin/rpm
    Failed to open: /usr/bin/rpm
    errno: Permission denied (13)
    # rpm --version
    -sh: /usr/bin/rpm: Permission denied

Enabling the audit kernel subsystem may help to debug appraisal
issues. Enable it by adding the meta-security-framework layer and
changing your local.conf:
    SRC_URI_append_pn-linux-yocto = " file://audit.cfg"
    CORE_IMAGE_EXTRA_INSTALL += "auditd"

Then boot with "ima_appraise=log ima_appraise_tcb".

Adding auditd is not strictly necessary but helps to capture a
more complete set of events in /var/log/audit/ and search in
them with ausearch.


3. Known Issues
===============

EVM is not enabled, for multiple reasons:
* Signing files in advance with a X509 certificate and then not having
  any confidential keys on the device would be the most useful mode,
  but is not supported by EVM [1].
* EVM signing in advance would only work on the final file system and thus
  will require further integration work with image creation. The content
  of the files can be signed for IMA in the rootfs, with the extended
  attributes remaining valid when copying the files to the final image.
  But for EVM that copy operation changes relevant parameters (for example,
  inode) and thus invalidates the EVM hash.
* On device creation of EVM hashes depends on secure key handling on the
  device (TPM) and booting at least once in a special mode (file system
  writable, evm=fix as boot parameter, reboot after opening all files);
  such a mode is too device specific to be implemented in a generic way.

IMA appraisal with "ima_appraise_tcb" enables rules which are too strict
for most distros. For example, systemd needs to write certain files
as root, which is prevented by the ima_appraise_tcb appraise rules. As
a result, the system fails to boot:

    [FAILED] Failed to start Commit a transient machine-id on disk.
    See "systemctl status systemd-machine-id-commit.service" for details.
    ...
    [FAILED] Failed to start Network Service.
    See "systemctl status systemd-networkd.service" for details.
    [FAILED] Failed to start Login Service.
    See "systemctl status systemd-logind.service" for details.

No package manager is integrated with IMA/EVM. When updating packages,
files will end up getting installed without correct IMA/EVM attributes
and thus will not be usable when appraisal is turned on.

[1] http://permalink.gmane.org/gmane.comp.handhelds.tizen.devel/6281
[2] http://permalink.gmane.org/gmane.comp.handhelds.tizen.devel/6275
