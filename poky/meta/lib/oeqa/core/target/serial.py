#
# SPDX-License-Identifier: MIT
#

import base64
import logging
import os
from threading import Lock
from . import OETarget

class OESerialTarget(OETarget):

    def __init__(self, logger, target_ip, server_ip, server_port=0,
                 timeout=300, serialcontrol_cmd=None, serialcontrol_extra_args=None,
                 serialcontrol_ps1=None, serialcontrol_connect_timeout=None, 
                 machine=None, **kwargs):
        if not logger:
            logger = logging.getLogger('target')
            logger.setLevel(logging.INFO)
            filePath = os.path.join(os.getcwd(), 'remoteTarget.log')
            fileHandler = logging.FileHandler(filePath, 'w', 'utf-8')
            formatter = logging.Formatter(
                        '%(asctime)s.%(msecs)03d %(levelname)s: %(message)s',
                        '%H:%M:%S')
            fileHandler.setFormatter(formatter)
            logger.addHandler(fileHandler)

        super(OESerialTarget, self).__init__(logger)

        if serialcontrol_ps1:
            self.target_ps1 = serialcontrol_ps1
        elif machine:
            # fallback to a default value which assumes root@machine
            self.target_ps1 = f'root@{machine}:.*# '
        else:
            raise ValueError("Unable to determine shell command prompt (PS1) format.")

        if not serialcontrol_cmd:
            raise ValueError("Unable to determine serial control command.")

        if serialcontrol_extra_args:
            self.connection_script = f'{serialcontrol_cmd} {serialcontrol_extra_args}'
        else:
            self.connection_script = serialcontrol_cmd

        if serialcontrol_connect_timeout:
            self.connect_timeout = serialcontrol_connect_timeout
        else:
            self.connect_timeout = 10 # default to 10s connection timeout

        self.default_command_timeout = timeout
        self.ip = target_ip
        self.server_ip = server_ip
        self.server_port = server_port 
        self.conn = None
        self.mutex = Lock()

    def start(self, **kwargs):
        pass

    def stop(self, **kwargs):
        pass

    def get_connection(self):
        if self.conn is None:
            self.conn = SerialConnection(self.connection_script,
                                         self.target_ps1,
                                         self.connect_timeout,
                                         self.default_command_timeout)

        return self.conn

    def run(self, cmd, timeout=None):
        """
            Runs command on target over the provided serial connection.
            The first call will open the connection, and subsequent
            calls will re-use the same connection to send new commands.

            command:    Command to run on target.
            timeout:    <value>:    Kill command after <val> seconds.
                        None:       Kill command default value seconds.
                        0:          No timeout, runs until return.
        """
        # Lock needed to avoid multiple threads running commands concurrently
        # A serial connection can only be used by one caller at a time
        with self.mutex:
            conn = self.get_connection()

            self.logger.debug(f"[Running]$ {cmd}")
            # Run the command, then echo $? to get the command's return code
            try:
                output = conn.run_command(cmd, timeout)
                status = conn.run_command("echo $?")
                self.logger.debug(f"   [stdout]: {output}")
                self.logger.debug(f"   [ret code]: {status}\n\n")
            except SerialTimeoutException as e:
                self.logger.debug(e)
                output = ""
                status = 255

            # Return to $HOME after each command to simulate a stateless SSH connection
            conn.run_command('cd "$HOME"')

        return (int(status), output)

    def copyTo(self, localSrc, remoteDst):
        """
            Copies files by converting them to base 32, then transferring
            the ASCII text to the target, and decoding it in place on the
            target.

            On a 115k baud serial connection, this method transfers at
            roughly 30kbps.
        """
        with open(localSrc, 'rb') as file:
            data = file.read()

        b32 = base64.b32encode(data).decode('utf-8')

        # To avoid shell line limits, send a chunk at a time
        SPLIT_LEN = 512 
        lines = [b32[i:i+SPLIT_LEN] for i in range(0, len(b32), SPLIT_LEN)]

        with self.mutex:
            conn = self.get_connection()

            filename = os.path.basename(localSrc)
            TEMP = f'/tmp/{filename}.b32'

            # Create or empty out the temp file
            conn.run_command(f'echo -n "" > {TEMP}')

            for line in lines:
                conn.run_command(f'echo -n {line} >> {TEMP}')

            # Check to see whether the remoteDst is a directory
            is_directory = conn.run_command(f'[[ -d {remoteDst} ]]; echo $?')
            if int(is_directory) == 0:
                # append the localSrc filename to the end of remoteDst
                remoteDst = os.path.join(remoteDst, filename)

            conn.run_command(f'base32 -d {TEMP} > {remoteDst}')
            conn.run_command(f'rm {TEMP}')

        return 0, 'Success'

    def copyFrom(self, remoteSrc, localDst):
        """
            Copies files by converting them to base 32 on the target, then
            transferring the ASCII text to the host. That text is then
            decoded here and written out to the destination.

            On a 115k baud serial connection, this method transfers at
            roughly 30kbps.
        """
        with self.mutex:
            b32 = self.get_connection().run_command(f'base32 {remoteSrc}')

            data = base64.b32decode(b32.replace('\r\n', ''))

            # If the local path is a directory, get the filename from
            # the remoteSrc path and append it to localDst
            if os.path.isdir(localDst):
                filename = os.path.basename(remoteSrc)
                localDst = os.path.join(localDst, filename)

            with open(localDst, 'wb') as file:
                file.write(data)

        return 0, 'Success'

    def copyDirTo(self, localSrc, remoteDst):
        """
            Copy recursively localSrc directory to remoteDst in target.
        """

        for root, dirs, files in os.walk(localSrc):
            # Create directories in the target as needed
            for d in dirs:
                tmpDir = os.path.join(root, d).replace(localSrc, "")
                newDir = os.path.join(remoteDst, tmpDir.lstrip("/"))
                cmd = "mkdir -p %s" % newDir
                self.run(cmd)

            # Copy files into the target
            for f in files:
                tmpFile = os.path.join(root, f).replace(localSrc, "")
                dstFile = os.path.join(remoteDst, tmpFile.lstrip("/"))
                srcFile = os.path.join(root, f)
                self.copyTo(srcFile, dstFile)

    def deleteFiles(self, remotePath, files):
        """
            Deletes files in target's remotePath.
        """

        cmd = "rm"
        if not isinstance(files, list):
            files = [files]

        for f in files:
            cmd = "%s %s" % (cmd, os.path.join(remotePath, f))

        self.run(cmd)

    def deleteDir(self, remotePath):
        """
            Deletes target's remotePath directory.
        """

        cmd = "rmdir %s" % remotePath
        self.run(cmd)

    def deleteDirStructure(self, localPath, remotePath):
        """
        Delete recursively localPath structure directory in target's remotePath.

        This function is useful to delete a package that is installed in the
        device under test (DUT) and the host running the test has such package
        extracted in tmp directory.

        Example:
            pwd: /home/user/tmp
            tree:   .
                    └── work
                        ├── dir1
                        │   └── file1
                        └── dir2

            localpath = "/home/user/tmp" and remotepath = "/home/user"

            With the above variables this function will try to delete the
            directory in the DUT in this order:
                /home/user/work/dir1/file1
                /home/user/work/dir1        (if dir is empty)
                /home/user/work/dir2        (if dir is empty)
                /home/user/work             (if dir is empty)
        """

        for root, dirs, files in os.walk(localPath, topdown=False):
            # Delete files first
            tmpDir = os.path.join(root).replace(localPath, "")
            remoteDir = os.path.join(remotePath, tmpDir.lstrip("/"))
            self.deleteFiles(remoteDir, files)

            # Remove dirs if empty
            for d in dirs:
                tmpDir = os.path.join(root, d).replace(localPath, "")
                remoteDir = os.path.join(remotePath, tmpDir.lstrip("/"))
                self.deleteDir(remoteDir)

class SerialTimeoutException(Exception):
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return self.msg

class SerialConnection:

    def __init__(self, script, target_prompt, connect_timeout, default_command_timeout):
        import pexpect # limiting scope to avoid build dependency
        self.prompt = target_prompt
        self.connect_timeout = connect_timeout
        self.default_command_timeout = default_command_timeout
        self.conn = pexpect.spawn('/bin/bash', ['-c', script], encoding='utf8')
        self._seek_to_clean_shell()
        # Disable echo to avoid the need to parse the outgoing command
        self.run_command('stty -echo')

    def _seek_to_clean_shell(self):
        """
            Attempts to find a clean shell, meaning it is clear and
            ready to accept a new command. This is necessary to ensure
            the correct output is captured from each command.
        """
        import pexpect # limiting scope to avoid build dependency
        # Look for a clean shell
        # Wait a short amount of time for the connection to finish
        pexpect_code = self.conn.expect([self.prompt, pexpect.TIMEOUT],
                                        timeout=self.connect_timeout)

        # if a timeout occurred, send an empty line and wait for a clean shell
        if pexpect_code == 1:
            # send a newline to clear and present the shell
            self.conn.sendline("")
            pexpect_code = self.conn.expect(self.prompt)

    def run_command(self, cmd, timeout=None):
        """
            Runs command on target over the provided serial connection.
            Returns any output on the shell while the command was run.

            command:    Command to run on target.
            timeout:    <value>:    Kill command after <val> seconds.
                        None:       Kill command default value seconds.
                        0:          No timeout, runs until return.
        """
        import pexpect # limiting scope to avoid build dependency
        # Convert from the OETarget defaults to pexpect timeout values
        if timeout is None:
            timeout = self.default_command_timeout
        elif timeout == 0:
            timeout = None # passing None to pexpect is infinite timeout

        self.conn.sendline(cmd)
        pexpect_code = self.conn.expect([self.prompt, pexpect.TIMEOUT], timeout=timeout)

        # check for timeout
        if pexpect_code == 1:
            self.conn.send('\003') # send Ctrl+C
            self._seek_to_clean_shell()
            raise SerialTimeoutException(f'Timeout executing: {cmd} after {timeout}s')

        return self.conn.before.removesuffix('\r\n')

