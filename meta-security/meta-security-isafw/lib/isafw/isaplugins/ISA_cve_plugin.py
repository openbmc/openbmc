#
# ISA_cve_plugin.py - CVE checker plugin, part of ISA FW
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
import os, sys
import re

CVEChecker = None
pkglist = "/cve_check_tool_pkglist"


class ISA_CVEChecker:
    initialized = False

    def __init__(self, ISA_config):
        self.cacert = ISA_config.cacert
        self.reportdir = ISA_config.reportdir
        self.timestamp = ISA_config.timestamp
        self.logfile = ISA_config.logdir + "/isafw_cvelog"
        self.report_name = ISA_config.reportdir + "/cve_report_" + \
            ISA_config.machine + "_" + ISA_config.timestamp
        self.initialized = True
        with open(self.logfile, 'a') as flog:
            flog.write("\nPlugin ISA_CVEChecker initialized!\n")
        output = ""
        # check that cve-check-tool is installed

    def process_package(self, ISA_pkg):
        if (self.initialized):
            if (ISA_pkg.name and ISA_pkg.version and ISA_pkg.patch_files):
                alias_pkgs_faux = []
                # need to compose faux format line for cve-check-tool
                cve_patch_info = self.process_patch_list(ISA_pkg.patch_files)
                pkgline_faux = ISA_pkg.name + "," + ISA_pkg.version + "," + cve_patch_info + ",\n"
                if ISA_pkg.aliases:
                    for a in ISA_pkg.aliases:
                        alias_pkgs_faux.append(
                            a + "," + ISA_pkg.version + "," + cve_patch_info + ",\n")
                pkglist_faux = pkglist + "_" + self.timestamp + ".faux"
                with open(self.reportdir + pkglist_faux, 'a') as fauxfile:
                    fauxfile.write(pkgline_faux)
                    for a in alias_pkgs_faux:
                        fauxfile.write(a)

                with open(self.logfile, 'a') as flog:
                    flog.write("\npkg info: " + pkgline_faux)
            else:
                self.initialized = False
                with open(self.logfile, 'a') as flog:
                    flog.write(
                        "Mandatory arguments such as pkg name, version and list of patches are not provided!\n")
                    flog.write("Not performing the call.\n")
        else:
            with open(self.logfile, 'a') as flog:
                flog.write(
                    "Plugin hasn't initialized! Not performing the call.\n")

    def process_report(self):
        if not os.path.isfile(self.reportdir + pkglist + "_" + self.timestamp + ".faux"):
            return
        if (self.initialized):
            with open(self.logfile, 'a') as flog:
                flog.write("Creating report in HTML format.\n")
            result = self.process_report_type("html")

            with open(self.logfile, 'a') as flog:
                flog.write("Creating report in CSV format.\n")
            result = self.process_report_type("csv")

            pkglist_faux = pkglist + "_" + self.timestamp + ".faux"
            os.remove(self.reportdir + pkglist_faux)

            with open(self.logfile, 'a') as flog:
                flog.write("Creating report in XML format.\n")
            self.write_report_xml(result)

    def write_report_xml(self, result):
        try:
            from lxml import etree
        except ImportError:
            try:
                import xml.etree.cElementTree as etree
            except ImportError:
                import xml.etree.ElementTree as etree
        num_tests = 0
        root = etree.Element('testsuite', name='CVE_Plugin', tests='1')

        if result :
            num_tests = 1
            tcase = etree.SubElement(
                        root, 'testcase', classname='ISA_CVEChecker', name="Error in cve-check-tool")
            etree.SubElement( tcase, 'failure', message=result, type='violation')
        else:
            with open(self.report_name + ".csv", 'r') as f:
                for line in f:
                    num_tests += 1
                    line = line.strip()
                    line_sp = line.split(',', 2)
                    if (len(line_sp) >= 3) and (line_sp[2].startswith('CVE')):
                        tcase = etree.SubElement(
                            root, 'testcase', classname='ISA_CVEChecker', name=line.split(',', 1)[0])
                        etree.SubElement(
                            tcase, 'failure', message=line, type='violation')
                    else:
                        tcase = etree.SubElement(
                            root, 'testcase', classname='ISA_CVEChecker', name=line.split(',', 1)[0])

        root.set('tests', str(num_tests))
        tree = etree.ElementTree(root)
        output = self.report_name + '.xml'
        try:
            tree.write(output, encoding='UTF-8',
                       pretty_print=True, xml_declaration=True)
        except TypeError:
            tree.write(output, encoding='UTF-8', xml_declaration=True)

    def process_report_type(self, rtype):
        # now faux file is ready and we can process it
        args = ""
        result = ""
        tool_stderr_value = ""
        args += "cve-check-tool "
        if self.cacert:
            args += "--cacert '%s' " % self.cacert
        if rtype != "html":
            args += "-c "
            rtype = "csv"
        pkglist_faux = pkglist + "_" + self.timestamp + ".faux"
        args += "-a -t faux '" + self.reportdir + pkglist_faux + "'"
        with open(self.logfile, 'a') as flog:
            flog.write("Args: " + args)
        try:
            popen = subprocess.Popen(
                args, shell=True, env=os.environ, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            result = popen.communicate()
        except:
            tool_stderr_value = "Error in executing cve-check-tool" + str(sys.exc_info())
            with open(self.logfile, 'a') as flog:
                flog.write("Error in executing cve-check-tool: " +
                           str(sys.exc_info()))
        else:
            stdout_value = result[0]
            tool_stderr_value = result[1].decode('utf-8')
            if not tool_stderr_value and popen.returncode == 0:
                report = self.report_name + "." + rtype
                with open(report, 'wb') as freport:
                    freport.write(stdout_value)
            else:
                tool_stderr_value = tool_stderr_value + \
                "\ncve-check-tool terminated with exit code " + str(popen.returncode)
        return tool_stderr_value

    def process_patch_list(self, patch_files):
        patch_info = ""
        for patch in patch_files:
            patch1 = patch.partition("cve")
            if (patch1[0] == patch):
                # no cve substring, try CVE
                patch1 = patch.partition("CVE")
                if (patch1[0] == patch):
                    continue
            patchstripped = patch1[2].split('-')
            try:
                patch_info += " CVE-" + \
                    patchstripped[1] + "-" + re.findall('\d+', patchstripped[2])[0]
            except IndexError:
                # string parsing attempt failed, so just skip this patch
               continue
        return patch_info

# ======== supported callbacks from ISA ============= #


def init(ISA_config):
    global CVEChecker
    CVEChecker = ISA_CVEChecker(ISA_config)


def getPluginName():
    return "ISA_CVEChecker"


def process_package(ISA_pkg):
    global CVEChecker
    return CVEChecker.process_package(ISA_pkg)


def process_report():
    global CVEChecker
    return CVEChecker.process_report()

# ==================================================== #
