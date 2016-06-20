"""Helper module for GPG signing"""
import os

import bb
import oe.utils

class LocalSigner(object):
    """Class for handling local (on the build host) signing"""
    def __init__(self, d):
        self.gpg_bin = d.getVar('GPG_BIN', True) or \
                  bb.utils.which(os.getenv('PATH'), 'gpg')
        self.gpg_path = d.getVar('GPG_PATH', True)
        self.rpm_bin = bb.utils.which(os.getenv('PATH'), "rpm")

    def export_pubkey(self, output_file, keyid, armor=True):
        """Export GPG public key to a file"""
        cmd = '%s --batch --yes --export -o %s ' % \
                (self.gpg_bin, output_file)
        if self.gpg_path:
            cmd += "--homedir %s " % self.gpg_path
        if armor:
            cmd += "--armor "
        cmd += keyid
        status, output = oe.utils.getstatusoutput(cmd)
        if status:
            raise bb.build.FuncFailed('Failed to export gpg public key (%s): %s' %
                                      (keyid, output))

    def sign_rpms(self, files, keyid, passphrase):
        """Sign RPM files"""

        cmd = self.rpm_bin + " --addsign --define '_gpg_name %s'  " % keyid
        cmd += "--define '_gpg_passphrase %s' " % passphrase
        if self.gpg_bin:
            cmd += "--define '%%__gpg %s' " % self.gpg_bin
        if self.gpg_path:
            cmd += "--define '_gpg_path %s' " % self.gpg_path
        cmd += ' '.join(files)

        status, output = oe.utils.getstatusoutput(cmd)
        if status:
            raise bb.build.FuncFailed("Failed to sign RPM packages: %s" % output)

    def detach_sign(self, input_file, keyid, passphrase_file, passphrase=None, armor=True):
        """Create a detached signature of a file"""
        import subprocess

        if passphrase_file and passphrase:
            raise Exception("You should use either passphrase_file of passphrase, not both")

        cmd = [self.gpg_bin, '--detach-sign', '--batch', '--no-tty', '--yes',
               '--passphrase-fd', '0', '-u', keyid]

        if self.gpg_path:
            cmd += ['--homedir', self.gpg_path]
        if armor:
            cmd += ['--armor']

        #gpg > 2.1 supports password pipes only through the loopback interface
        #gpg < 2.1 errors out if given unknown parameters
        dots = self.get_gpg_version().split('.')
        assert len(dots) >= 2
        if int(dots[0]) >= 2 and int(dots[1]) >= 1:
            cmd += ['--pinentry-mode', 'loopback']

        cmd += [input_file]

        try:
            if passphrase_file:
                with open(passphrase_file) as fobj:
                    passphrase = fobj.readline();

            job = subprocess.Popen(cmd, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
            (_, stderr) = job.communicate(passphrase)

            if job.returncode:
                raise bb.build.FuncFailed("GPG exited with code %d: %s" %
                                          (job.returncode, stderr))

        except IOError as e:
            bb.error("IO error (%s): %s" % (e.errno, e.strerror))
            raise Exception("Failed to sign '%s'" % input_file)

        except OSError as e:
            bb.error("OS error (%s): %s" % (e.errno, e.strerror))
            raise Exception("Failed to sign '%s" % input_file)


    def get_gpg_version(self):
        """Return the gpg version"""
        import subprocess
        try:
            return subprocess.check_output((self.gpg_bin, "--version")).split()[2]
        except subprocess.CalledProcessError as e:
            raise bb.build.FuncFailed("Could not get gpg version: %s" % e)


    def verify(self, sig_file):
        """Verify signature"""
        cmd = self.gpg_bin + " --verify "
        if self.gpg_path:
            cmd += "--homedir %s " % self.gpg_path
        cmd += sig_file
        status, _ = oe.utils.getstatusoutput(cmd)
        ret = False if status else True
        return ret


def get_signer(d, backend):
    """Get signer object for the specified backend"""
    # Use local signing by default
    if backend == 'local':
        return LocalSigner(d)
    else:
        bb.fatal("Unsupported signing backend '%s'" % backend)

