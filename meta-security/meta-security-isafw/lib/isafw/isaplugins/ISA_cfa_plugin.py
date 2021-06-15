#
# ISA_cfa_plugin.py - Compile flag analyzer plugin, part of ISA FW
# Main functionality is based on build_comp script from Clear linux project
#
# Copyright (c) 2015 - 2016, Intel Corporation
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
#    * Redistributions of source code must retain the above copyright notice,
#      this list of conditions and the following disclaimer.
#    * Redistributions in binary form must reproduce the above copyright
#      notice, this list of conditions and the following disclaimer in the
#      documentation and/or other materials provided with the distribution.
#    * Neither the name of Intel Corporation nor the names of its contributors
#      may be used to endorse or promote products derived from this software
#      without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import subprocess
import os
import sys
import re
import copy
try:
    from lxml import etree
except ImportError:
    try:
        import xml.etree.cElementTree as etree
    except ImportError:
        import xml.etree.ElementTree as etree


CFChecker = None


class ISA_CFChecker():
    initialized = False
    no_relro = []
    partial_relro = []
    no_canary = []
    no_pie = []
    execstack = []
    execstack_not_defined = []
    nodrop_groups = []
    no_mpx = []

    def __init__(self, ISA_config):
        self.logfile = ISA_config.logdir + "/isafw_cfalog"
        self.full_report_name = ISA_config.reportdir + "/cfa_full_report_" + \
            ISA_config.machine + "_" + ISA_config.timestamp
        self.problems_report_name = ISA_config.reportdir + \
            "/cfa_problems_report_" + ISA_config.machine + "_" + ISA_config.timestamp
        self.full_reports = ISA_config.full_reports
        self.ISA_filesystem = ""
        # check that checksec and other tools are installed
        tools_errors = _check_tools()
        if tools_errors:
            with open(self.logfile, 'w') as flog:
                flog.write(tools_errors)
                return
        self.initialized = True
        with open(self.logfile, 'w') as flog:
            flog.write("\nPlugin ISA_CFChecker initialized!\n")
        return

    def process_filesystem(self, ISA_filesystem):
        self.ISA_filesystem = ISA_filesystem
        fs_path = self.ISA_filesystem.path_to_fs
        img_name = self.ISA_filesystem.img_name
        if (self.initialized):
            if (img_name and fs_path):
                with open(self.logfile, 'a') as flog:
                    flog.write("\n\nFilesystem path is: " + fs_path)
                if self.full_reports:
                    with open(self.full_report_name + "_" + img_name, 'w') as ffull_report:
                        ffull_report.write(
                            "Security-relevant flags for executables for image: " + img_name + '\n')
                        ffull_report.write("With rootfs location at " + fs_path + "\n\n")
                files = self.find_files(fs_path)
                import multiprocessing
                pool = multiprocessing.Pool()
                results = pool.imap(process_file_wrapper, files)
                pool.close()
                pool.join()
                self.process_results(results)
            else:
                with open(self.logfile, 'a') as flog:
                    flog.write(
                        "Mandatory arguments such as image name and path to the filesystem are not provided!\n")
                    flog.write("Not performing the call.\n")
        else:
            with open(self.logfile, 'a') as flog:
                flog.write("Plugin hasn't initialized! Not performing the call.\n")

    def process_results(self, results):
        fs_path = self.ISA_filesystem.path_to_fs
        for result in results:
            if not result:
                with open(self.logfile, 'a') as flog:
                    flog.write("\nError in returned result")
                continue
            with open(self.logfile, 'a') as flog:
                flog.write("\n\nFor file: " + str(result[0]) + "\nlog is: " + str(result[5]))
            if result[1]:
                with open(self.logfile, 'a') as flog:
                    flog.write("\n\nsec_field: " + str(result[1]))
                if "No RELRO" in result[1]:
                    self.no_relro.append(result[0].replace(fs_path, ""))
                elif "Partial RELRO" in result[1]:
                    self.partial_relro.append(result[0].replace(fs_path, ""))
                if "No canary found" in result[1]:
                    self.no_canary.append(result[0].replace(fs_path, ""))
                if "No PIE" in result[1]:
                    self.no_pie.append(result[0].replace(fs_path, ""))
            if result[2]:
                if result[2] == "execstack":
                    self.execstack.append(result[0].replace(fs_path, ""))
                elif result[2] == "not_defined":
                    self.execstack_not_defined.append(result[0].replace(fs_path, ""))
            if result[3] and (result[3] == True):
                self.nodrop_groups.append(result[0].replace(fs_path, ""))
            if result[4] and (result[4] == True):
                self.no_mpx.append(result[0].replace(fs_path, ""))
            self.write_full_report(result)
        self.write_report()
        self.write_report_xml()

    def write_full_report(self, result):
        if not self.full_reports:
            return
        fs_path = self.ISA_filesystem.path_to_fs
        img_name = self.ISA_filesystem.img_name
        with open(self.full_report_name + "_" + img_name, 'a') as ffull_report:
            ffull_report.write('\nFile: ' + result[0].replace(fs_path, ""))
            ffull_report.write('\nsecurity flags: ' + str(result[1]))
            ffull_report.write('\nexecstack: ' + str(result[2]))
            ffull_report.write('\nnodrop_groups: ' + str(result[3]))
            ffull_report.write('\nno mpx: ' + str(result[4]))
            ffull_report.write('\n')

    def write_report(self):
        fs_path = self.ISA_filesystem.path_to_fs
        img_name = self.ISA_filesystem.img_name
        with open(self.problems_report_name + "_" + img_name, 'w') as fproblems_report:
            fproblems_report.write("Report for image: " + img_name + '\n')
            fproblems_report.write("With rootfs location at " + fs_path + "\n\n")
            fproblems_report.write("Relocation Read-Only\n")
            fproblems_report.write("More information about RELRO and how to enable it:")
            fproblems_report.write(
                " http://tk-blog.blogspot.de/2009/02/relro-not-so-well-known-memory.html\n")
            fproblems_report.write("Files with no RELRO:\n")
            for item in self.no_relro:
                fproblems_report.write(item + '\n')
            fproblems_report.write("Files with partial RELRO:\n")
            for item in self.partial_relro:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nStack protection\n")
            fproblems_report.write(
                "More information about canary stack protection and how to enable it:")
            fproblems_report.write("https://lwn.net/Articles/584225/ \n")
            fproblems_report.write("Files with no canary:\n")
            for item in self.no_canary:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nPosition Independent Executable\n")
            fproblems_report.write("More information about PIE protection and how to enable it:")
            fproblems_report.write(
                "https://securityblog.redhat.com/2012/11/28/position-independent-executables-pie/\n")
            fproblems_report.write("Files with no PIE:\n")
            for item in self.no_pie:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nNon-executable stack\n")
            fproblems_report.write("Files with executable stack enabled:\n")
            for item in self.execstack:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nFiles with no ability to fetch executable stack status:\n")
            for item in self.execstack_not_defined:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nGrop initialization:\n")
            fproblems_report.write(
                "If using setuid/setgid calls in code, one must call initgroups or setgroups\n")
            fproblems_report.write(
                "Files that don't initialize groups while using setuid/setgid:\n")
            for item in self.nodrop_groups:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nMemory Protection Extensions\n")
            fproblems_report.write("More information about MPX protection and how to enable it:")
            fproblems_report.write(
                "https://software.intel.com/sites/default/files/managed/9d/f6/Intel_MPX_EnablingGuide.pdf\n")
            fproblems_report.write("Files that don't have MPX protection enabled:\n")
            for item in self.no_mpx:
                fproblems_report.write(item + '\n')

    def write_report_xml(self):
        numTests = len(self.no_relro) + len(self.partial_relro) + len(self.no_canary) + len(self.no_pie) + \
            len(self.execstack) + len(self.execstack_not_defined) + \
            len(self.nodrop_groups) + len(self.no_mpx)
        root = etree.Element('testsuite', name='ISA_CFChecker', tests=str(numTests))
        if self.no_relro:
            for item in self.no_relro:
                tcase1 = etree.SubElement(
                    root, 'testcase', classname='files_with_no_RELRO', name=item)
                etree.SubElement(tcase1, 'failure', message=item, type='violation')
        if self.partial_relro:
            for item in self.partial_relro:
                tcase1 = etree.SubElement(
                    root, 'testcase', classname='files_with_partial_RELRO', name=item)
                etree.SubElement(tcase1, 'failure', message=item, type='violation')
        if self.no_canary:
            for item in self.no_canary:
                tcase2 = etree.SubElement(
                    root, 'testcase', classname='files_with_no_canary', name=item)
                etree.SubElement(tcase2, 'failure', message=item, type='violation')
        if self.no_pie:
            for item in self.no_pie:
                tcase3 = etree.SubElement(
                    root, 'testcase', classname='files_with_no_PIE', name=item)
                etree.SubElement(tcase3, 'failure', message=item, type='violation')
        if self.execstack:
            for item in self.execstack:
                tcase5 = etree.SubElement(
                    root, 'testcase', classname='files_with_execstack', name=item)
                etree.SubElement(tcase5, 'failure', message=item, type='violation')
        if self.execstack_not_defined:
            for item in self.execstack_not_defined:
                tcase6 = etree.SubElement(
                    root, 'testcase', classname='files_with_execstack_not_defined', name=item)
                etree.SubElement(tcase6, 'failure', message=item, type='violation')
        if self.nodrop_groups:
            for item in self.nodrop_groups:
                tcase7 = etree.SubElement(
                    root, 'testcase', classname='files_with_nodrop_groups', name=item)
                etree.SubElement(tcase7, 'failure', message=item, type='violation')
        if self.no_mpx:
            for item in self.no_mpx:
                tcase8 = etree.SubElement(
                    root, 'testcase', classname='files_with_no_mpx', name=item)
                etree.SubElement(tcase8, 'failure', message=item, type='violation')
        tree = etree.ElementTree(root)
        output = self.problems_report_name + "_" + self.ISA_filesystem.img_name + '.xml'
        try:
            tree.write(output, encoding='UTF-8', pretty_print=True, xml_declaration=True)
        except TypeError:
            tree.write(output, encoding='UTF-8', xml_declaration=True)

    def find_files(self, init_path):
        list_of_files = []
        for (dirpath, dirnames, filenames) in os.walk(init_path):
            for f in filenames:
                list_of_files.append(str(dirpath + "/" + f)[:])
        return list_of_files


def _check_tools():

    def _is_in_path(executable):
        "Check for presence of executable in PATH"
        for path in os.environ["PATH"].split(os.pathsep):
            path = path.strip('"')
            if (os.path.isfile(os.path.join(path, executable)) and
                    os.access(os.path.join(path, executable), os.X_OK)):
                return True
        return False

    tools = {
        "checksec.sh": "Please install checksec from http://www.trapkit.de/tools/checksec.html\n",
        "execstack": "Please install execstack from prelink package\n",
        "readelf": "Please install binutils\n",
        "objdump": "Please install binutils\n",
    }
    output = ""
    for tool in tools:
        if not _is_in_path(tool):
            output += tools[tool]
    return output


def get_info(tool, args, file_name):
    env = copy.deepcopy(os.environ)
    env['PSEUDO_UNLOAD'] = "1"
    cmd = [tool, args, file_name]
    with open(os.devnull, 'wb') as DEVNULL:
        try:
            result = subprocess.check_output(cmd, stderr=DEVNULL, env=env).decode('utf-8')
        except:
            return ""
        else:
            return result

def get_security_flags(file_name):
    env = copy.deepcopy(os.environ)
    env['PSEUDO_UNLOAD'] = "1"
    cmd = ['checksec.sh', '--file', file_name]
    try:
        result = subprocess.check_output(cmd, env=env).decode('utf-8').splitlines()[1]
    except:
        return "Not able to fetch flags"
    else:
        # remove ansi escape color sequences
        result = re.sub(r'\x1b[^m]*m', '', result)
        return re.split(r' {2,}', result)[:-1]


def process_file(file):
    log = "File from map " + file
    fun_results = [file, [], "", False, False, log]
    if not os.path.isfile(file):
        return fun_results
    env = copy.deepcopy(os.environ)
    env['PSEUDO_UNLOAD'] = "1"
    # getting file type
    cmd = ['file', '--mime-type', file]
    try:
        result = subprocess.check_output(cmd, env=env).decode('utf-8')
    except:
        fun_results[-1] += "\nNot able to decode mime type"
        return fun_results
    file_type = result.split()[-1]
    # looking for links
    if "symlink" in file_type:
        file = os.path.realpath(file)
        cmd = ['file', '--mime-type', file]
        try:
            result = subprocess.check_output(cmd, env=env).decode('utf-8')
        except:
            fun_results[-1] += "\nNot able to decode mime type"
            return fun_results
        file_type = result.split()[-1]
    # checking security flags if applies
    if "application" not in file_type:
        return fun_results
    fun_results[-1] += "\nFile type: " + file_type
    if (("octet-stream" in file_type) or ("dosexec" in file_type) or
            ("archive" in file_type) or ("xml" in file_type) or
            ("gzip" in file_type) or ("postscript" in file_type) or
            ("pdf" in file_type)):
        return fun_results
    fun_results[1] = get_security_flags(file)
    tmp = get_info("execstack", '-q', file)
    if tmp.startswith("X "):
        fun_results[2] = "execstack"
    elif tmp.startswith("? "):
        fun_results[2] = "not_defined"
    tmp = get_info("readelf", '-s', file)
    if ("setgid@GLIBC" in tmp) or ("setegid@GLIBC" in tmp) or ("setresgid@GLIBC" in tmp):
        if ("setuid@GLIBC" in tmp) or ("seteuid@GLIBC" in tmp) or ("setresuid@GLIBC" in tmp):
            if ("setgroups@GLIBC" not in tmp) and ("initgroups@GLIBC" not in tmp):
                fun_results[3] = True
    tmp = get_info("objdump", '-d', file)
    if ("bndcu" not in tmp) and ("bndcl" not in tmp) and ("bndmov" not in tmp):
        fun_results[4] = True
    return fun_results

def process_file_wrapper(file):
    # Ensures that exceptions get logged with the original backtrace.
    # Without this, they appear with a backtrace rooted in
    # the code which transfers back the result to process_results().
    try:
        return process_file(file)
    except:
        from isafw import isafw
        import traceback
        isafw.error('Internal error:\n%s' % traceback.format_exc())
        raise

# ======== supported callbacks from ISA ============ #


def init(ISA_config):
    global CFChecker
    CFChecker = ISA_CFChecker(ISA_config)


def getPluginName():
    return "ISA_CFChecker"


def process_filesystem(ISA_filesystem):
    global CFChecker
    return CFChecker.process_filesystem(ISA_filesystem)

# =================================================== #
