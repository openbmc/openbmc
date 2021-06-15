#
# ISA_fsa_plugin.py - Filesystem analyser plugin, part of ISA FW
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
import os
from stat import *
try:
    from lxml import etree
except ImportError:
    try:
        import xml.etree.cElementTree as etree
    except ImportError:
        import xml.etree.ElementTree as etree


FSAnalyzer = None


class ISA_FSChecker():
    initialized = False

    def __init__(self, ISA_config):
        self.logfile = ISA_config.logdir + "/isafw_fsalog"
        self.full_report_name = ISA_config.reportdir + "/fsa_full_report_" + \
            ISA_config.machine + "_" + ISA_config.timestamp
        self.problems_report_name = ISA_config.reportdir + \
            "/fsa_problems_report_" + ISA_config.machine + "_" + ISA_config.timestamp
        self.full_reports = ISA_config.full_reports
        self.initialized = True
        self.setuid_files = []
        self.setgid_files = []
        self.ww_files = []
        self.no_sticky_bit_ww_dirs = []
        with open(self.logfile, 'w') as flog:
            flog.write("\nPlugin ISA_FSChecker initialized!\n")

    def process_filesystem(self, ISA_filesystem):
        if (self.initialized):
            if (ISA_filesystem.img_name and ISA_filesystem.path_to_fs):
                with open(self.logfile, 'a') as flog:
                    flog.write("Analyzing filesystem at: " + ISA_filesystem.path_to_fs +
                               " for the image: " + ISA_filesystem.img_name + "\n")
                self.files = self.find_fsobjects(ISA_filesystem.path_to_fs)
                with open(self.logfile, 'a') as flog:
                    flog.write("\nFilelist is: " + str(self.files))
                if self.full_reports:
                    with open(self.full_report_name + "_" + ISA_filesystem.img_name, 'w') as ffull_report:
                        ffull_report.write(
                            "Report for image: " + ISA_filesystem.img_name + '\n')
                        ffull_report.write(
                            "With rootfs location at " + ISA_filesystem.path_to_fs + "\n\n")
                for f in self.files:
                    st = os.lstat(f)
                    i = f.replace(ISA_filesystem.path_to_fs, "")
                    if self.full_reports:
                        with open(self.full_report_name + "_" + ISA_filesystem.img_name, 'a') as ffull_report:
                            ffull_report.write("File: " + i + ' mode: ' + str(oct(st.st_mode)) +
                                               " uid: " + str(st.st_uid) + " gid: " + str(st.st_gid) + '\n')
                    if ((st.st_mode & S_ISUID) == S_ISUID):
                        self.setuid_files.append(i)
                    if ((st.st_mode & S_ISGID) == S_ISGID):
                        self.setgid_files.append(i)
                    if ((st.st_mode & S_IWOTH) == S_IWOTH):
                        if (((st.st_mode & S_IFDIR) == S_IFDIR) and ((st.st_mode & S_ISVTX) != S_ISVTX)):
                            self.no_sticky_bit_ww_dirs.append(i)
                        if (((st.st_mode & S_IFREG) == S_IFREG) and ((st.st_mode & S_IFLNK) != S_IFLNK)):
                            self.ww_files.append(i)
                self.write_problems_report(ISA_filesystem)
                self.write_problems_report_xml(ISA_filesystem)
            else:
                with open(self.logfile, 'a') as flog:
                    flog.write(
                        "Mandatory arguments such as image name and path to the filesystem are not provided!\n")
                    flog.write("Not performing the call.\n")
        else:
            with open(self.logfile, 'a') as flog:
                flog.write(
                    "Plugin hasn't initialized! Not performing the call.\n")

    def write_problems_report(self, ISA_filesystem):
        with open(self.problems_report_name + "_" + ISA_filesystem.img_name, 'w') as fproblems_report:
            fproblems_report.write(
                "Report for image: " + ISA_filesystem.img_name + '\n')
            fproblems_report.write(
                "With rootfs location at " + ISA_filesystem.path_to_fs + "\n\n")
            fproblems_report.write("Files with SETUID bit set:\n")
            for item in self.setuid_files:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nFiles with SETGID bit set:\n")
            for item in self.setgid_files:
                fproblems_report.write(item + '\n')
            fproblems_report.write("\n\nWorld-writable files:\n")
            for item in self.ww_files:
                fproblems_report.write(item + '\n')
            fproblems_report.write(
                "\n\nWorld-writable dirs with no sticky bit:\n")
            for item in self.no_sticky_bit_ww_dirs:
                fproblems_report.write(item + '\n')

    def write_problems_report_xml(self, ISA_filesystem):
        num_tests = len(self.setuid_files) + len(self.setgid_files) + \
            len(self.ww_files) + len(self.no_sticky_bit_ww_dirs)
        root = etree.Element(
            'testsuite', name='FSA_Plugin', tests=str(num_tests))
        if self.setuid_files:
            for item in self.setuid_files:
                tcase1 = etree.SubElement(
                    root, 'testcase', classname='Files_with_SETUID_bit_set', name=item)
                etree.SubElement(
                    tcase1, 'failure', message=item, type='violation')
        if self.setgid_files:
            for item in self.setgid_files:
                tcase2 = etree.SubElement(
                    root, 'testacase', classname='Files_with_SETGID_bit_set', name=item)
                etree.SubElement(
                    tcase2, 'failure', message=item, type='violation')
        if self.ww_files:
            for item in self.ww_files:
                tcase3 = etree.SubElement(
                    root, 'testase', classname='World-writable_files', name=item)
                etree.SubElement(
                    tcase3, 'failure', message=item, type='violation')
        if self.no_sticky_bit_ww_dirs:
            for item in self.no_sticky_bit_ww_dirs:
                tcase4 = etree.SubElement(
                    root, 'testcase', classname='World-writable_dirs_with_no_sticky_bit', name=item)
                etree.SubElement(
                    tcase4, 'failure', message=item, type='violation')
        tree = etree.ElementTree(root)
        output = self.problems_report_name + "_" + ISA_filesystem.img_name + '.xml'
        try:
            tree.write(output, encoding='UTF-8',
                       pretty_print=True, xml_declaration=True)
        except TypeError:
            tree.write(output, encoding='UTF-8', xml_declaration=True)

    def find_fsobjects(self, init_path):
        list_of_files = []
        for (dirpath, dirnames, filenames) in os.walk(init_path):
            if (dirpath != init_path):
                list_of_files.append(str(dirpath)[:])
            for f in filenames:
                list_of_files.append(str(dirpath + "/" + f)[:])
        return list_of_files

# ======== supported callbacks from ISA ============= #


def init(ISA_config):
    global FSAnalyzer
    FSAnalyzer = ISA_FSChecker(ISA_config)


def getPluginName():
    return "ISA_FSChecker"


def process_filesystem(ISA_filesystem):
    global FSAnalyzer
    return FSAnalyzer.process_filesystem(ISA_filesystem)

# ==================================================== #
