# Class for generating signed RPM packages.
#
# Configuration variables used by this class:
# RPM_GPG_PASSPHRASE_FILE
#           Path to a file containing the passphrase of the signing key.
# RPM_GPG_NAME
#           Name of the key to sign with. Alternatively you can define
#           %_gpg_name macro in your ~/.oerpmmacros file.
# RPM_GPG_PUBKEY
#           Path to a file containing the public key (in "armor" format)
#           corresponding the signing key.
# GPG_BIN
#           Optional variable for specifying the gpg binary/wrapper to use for
#           signing.
#
inherit sanity

RPM_SIGN_PACKAGES='1'


_check_gpg_name () {
    macrodef=`rpm -E '%_gpg_name'`
    [ "$macrodef" == "%_gpg_name" ] && return 1 || return 0
}


def rpmsign_wrapper(d, files, passphrase, gpg_name=None):
    import pexpect

    # Find the correct rpm binary
    rpm_bin_path = d.getVar('STAGING_BINDIR_NATIVE', True) + '/rpm'
    cmd = rpm_bin_path + " --addsign "
    if gpg_name:
        cmd += "--define '%%_gpg_name %s' " % gpg_name
    else:
        try:
            bb.build.exec_func('_check_gpg_name', d)
        except bb.build.FuncFailed:
            raise_sanity_error("You need to define RPM_GPG_NAME in bitbake "
                               "config or the %_gpg_name RPM macro defined "
                               "(e.g. in  ~/.oerpmmacros", d)
    if d.getVar('GPG_BIN', True):
        cmd += "--define '%%__gpg %s' " % d.getVar('GPG_BIN', True)
    cmd += ' '.join(files)

    # Need to use pexpect for feeding the passphrase
    proc = pexpect.spawn(cmd)
    try:
        proc.expect_exact('Enter pass phrase:', timeout=15)
        proc.sendline(passphrase)
        proc.expect(pexpect.EOF, timeout=900)
        proc.close()
    except pexpect.TIMEOUT as err:
        bb.debug('rpmsign timeout: %s' % err)
        proc.terminate()
    return proc.exitstatus


python sign_rpm () {
    import glob

    rpm_gpg_pass_file = (d.getVar("RPM_GPG_PASSPHRASE_FILE", True) or "")
    if rpm_gpg_pass_file:
        with open(rpm_gpg_pass_file) as fobj:
            rpm_gpg_passphrase = fobj.readlines()[0].rstrip('\n')
    else:
        raise_sanity_error("You need to define RPM_GPG_PASSPHRASE_FILE in the config", d)

    rpm_gpg_name = (d.getVar("RPM_GPG_NAME", True) or "")

    rpms = glob.glob(d.getVar('RPM_PKGWRITEDIR', True) + '/*')

    if rpmsign_wrapper(d, rpms, rpm_gpg_passphrase, rpm_gpg_name) != 0:
        raise bb.build.FuncFailed("RPM signing failed")
}
