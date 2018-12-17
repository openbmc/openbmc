"""Helper module for GPG signing"""
import os

import bb
import oe.utils
import subprocess
import shlex

class LocalSigner(object):
    """Class for handling local (on the build host) signing"""
    def __init__(self, d):
        self.gpg_bin = d.getVar('GPG_BIN') or \
                  bb.utils.which(os.getenv('PATH'), 'gpg')
        self.gpg_path = d.getVar('GPG_PATH')
        self.gpg_version = self.get_gpg_version()
        self.rpm_bin = bb.utils.which(os.getenv('PATH'), "rpmsign")
        self.gpg_agent_bin = bb.utils.which(os.getenv('PATH'), "gpg-agent")

    def export_pubkey(self, output_file, keyid, armor=True):
        """Export GPG public key to a file"""
        cmd = '%s --no-permission-warning --batch --yes --export -o %s ' % \
                (self.gpg_bin, output_file)
        if self.gpg_path:
            cmd += "--homedir %s " % self.gpg_path
        if armor:
            cmd += "--armor "
        cmd += keyid
        subprocess.check_output(shlex.split(cmd), stderr=subprocess.STDOUT)

    def sign_rpms(self, files, keyid, passphrase, digest, sign_chunk, fsk=None, fsk_password=None):
        """Sign RPM files"""

        cmd = self.rpm_bin + " --addsign --define '_gpg_name %s'  " % keyid
        gpg_args = '--no-permission-warning --batch --passphrase=%s --agent-program=%s|--auto-expand-secmem' % (passphrase, self.gpg_agent_bin)
        if self.gpg_version > (2,1,):
            gpg_args += ' --pinentry-mode=loopback'
        cmd += "--define '_gpg_sign_cmd_extra_args %s' " % gpg_args
        cmd += "--define '_binary_filedigest_algorithm %s' " % digest
        if self.gpg_bin:
            cmd += "--define '__gpg %s' " % self.gpg_bin
        if self.gpg_path:
            cmd += "--define '_gpg_path %s' " % self.gpg_path
        if fsk:
            cmd += "--signfiles --fskpath %s " % fsk
            if fsk_password:
                cmd += "--define '_file_signing_key_password %s' " % fsk_password

        # Sign in chunks
        for i in range(0, len(files), sign_chunk):
            subprocess.check_output(shlex.split(cmd + ' '.join(files[i:i+sign_chunk])), stderr=subprocess.STDOUT)

    def detach_sign(self, input_file, keyid, passphrase_file, passphrase=None, armor=True):
        """Create a detached signature of a file"""

        if passphrase_file and passphrase:
            raise Exception("You should use either passphrase_file of passphrase, not both")

        cmd = [self.gpg_bin, '--detach-sign', '--no-permission-warning', '--batch',
               '--no-tty', '--yes', '--passphrase-fd', '0', '-u', keyid]

        if self.gpg_path:
            cmd += ['--homedir', self.gpg_path]
        if armor:
            cmd += ['--armor']

        #gpg > 2.1 supports password pipes only through the loopback interface
        #gpg < 2.1 errors out if given unknown parameters
        if self.gpg_version > (2,1,):
            cmd += ['--pinentry-mode', 'loopback']

        if self.gpg_agent_bin:
            cmd += ["--agent-program=%s|--auto-expand-secmem" % (self.gpg_agent_bin)]

        cmd += [input_file]

        try:
            if passphrase_file:
                with open(passphrase_file) as fobj:
                    passphrase = fobj.readline();

            job = subprocess.Popen(cmd, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
            (_, stderr) = job.communicate(passphrase.encode("utf-8"))

            if job.returncode:
                raise bb.build.FuncFailed("GPG exited with code %d: %s" %
                                          (job.returncode, stderr.decode("utf-8")))

        except IOError as e:
            bb.error("IO error (%s): %s" % (e.errno, e.strerror))
            raise Exception("Failed to sign '%s'" % input_file)

        except OSError as e:
            bb.error("OS error (%s): %s" % (e.errno, e.strerror))
            raise Exception("Failed to sign '%s" % input_file)


    def get_gpg_version(self):
        """Return the gpg version as a tuple of ints"""
        try:
            ver_str = subprocess.check_output((self.gpg_bin, "--version", "--no-permission-warning")).split()[2].decode("utf-8")
            return tuple([int(i) for i in ver_str.split("-")[0].split('.')])
        except subprocess.CalledProcessError as e:
            raise bb.build.FuncFailed("Could not get gpg version: %s" % e)


    def verify(self, sig_file):
        """Verify signature"""
        cmd = self.gpg_bin + " --verify --no-permission-warning "
        if self.gpg_path:
            cmd += "--homedir %s " % self.gpg_path
        cmd += sig_file
        status = subprocess.call(shlex.split(cmd))
        ret = False if status else True
        return ret


def get_signer(d, backend):
    """Get signer object for the specified backend"""
    # Use local signing by default
    if backend == 'local':
        return LocalSigner(d)
    else:
        bb.fatal("Unsupported signing backend '%s'" % backend)
