#
# ISA_la_plugin.py - License analyzer plugin, part of ISA FW
# Functionality is based on similar scripts from Clear linux project
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

LicenseChecker = None

flicenses = "/configs/la/licenses"
fapproved_non_osi = "/configs/la/approved-non-osi"
fexceptions = "/configs/la/exceptions"
funwanted = "/configs/la/violations"


class ISA_LicenseChecker():
    initialized = False
    rpm_present = False

    def __init__(self, ISA_config):
        self.logfile = ISA_config.logdir + "/isafw_lalog"
        self.unwanted = []
        self.report_name = ISA_config.reportdir + "/la_problems_report_" + \
            ISA_config.machine + "_" + ISA_config.timestamp
        self.image_pkg_list = ISA_config.reportdir + "/pkglist"
        self.image_pkgs = []
        self.la_plugin_image_whitelist = ISA_config.la_plugin_image_whitelist
        self.la_plugin_image_blacklist = ISA_config.la_plugin_image_blacklist
        self.initialized = True
        with open(self.logfile, 'a') as flog:
            flog.write("\nPlugin ISA_LA initialized!\n")
        # check that rpm is installed (supporting only rpm packages for now)
        DEVNULL = open(os.devnull, 'wb')
        rc = subprocess.call(["which", "rpm"], stdout=DEVNULL, stderr=DEVNULL)
        DEVNULL.close()
        if rc == 0:
            self.rpm_present = True
        else:
            with open(self.logfile, 'a') as flog:
                flog.write("rpm tool is missing! Licence info is expected from build system\n")

    def process_package(self, ISA_pkg):
        if (self.initialized):
            if ISA_pkg.name:
                if (not ISA_pkg.licenses):
                    # need to determine licenses first
                    # for this we need rpm tool to be present
                    if (not self.rpm_present):
                        with open(self.logfile, 'a') as flog:
                            flog.write("rpm tool is missing and licence info is not provided. Cannot proceed.\n")
                            return;     
                    if (not ISA_pkg.source_files):
                        if (not ISA_pkg.path_to_sources):
                            self.initialized = False
                            with open(self.logfile, 'a') as flog:
                                flog.write(
                                    "No path to sources or source file list is provided!")
                                flog.write(
                                    "\nNot able to determine licenses for package: " + ISA_pkg.name)
                            return
                        # need to build list of source files
                        ISA_pkg.source_files = self.find_files(
                            ISA_pkg.path_to_sources)
                    for i in ISA_pkg.source_files:
                        if (i.endswith(".spec")):# supporting rpm only for now
                            args = ("rpm", "-q", "--queryformat",
                                    "%{LICENSE} ", "--specfile", i)
                            try:
                                popen = subprocess.Popen(
                                    args, stdout=subprocess.PIPE)
                                popen.wait()
                                ISA_pkg.licenses = popen.stdout.read().split()
                            except:
                                self.initialized = False
                                with open(self.logfile, 'a') as flog:
                                    flog.write(
                                        "Error in executing rpm query: " + str(sys.exc_info()))
                                    flog.write(
                                        "\nNot able to process package: " + ISA_pkg.name)
                                return
                for l in ISA_pkg.licenses:
                    if (not self.check_license(l, flicenses) and
                            not self.check_license(l, fapproved_non_osi) and
                            not self.check_exceptions(ISA_pkg.name, l, fexceptions)):
                        # log the package as not following correct license
                        with open(self.report_name, 'a') as freport:
                            freport.write(l + "\n")
                    if (self.check_license(l, funwanted)):
                        # log the package as having license that should not be
                        # used
                        with open(self.report_name + "_unwanted", 'a') as freport:
                            freport.write(l + "\n")
            else:
                self.initialized = False
                with open(self.logfile, 'a') as flog:
                    flog.write(
                        "Mandatory argument package name is not provided!\n")
                    flog.write("Not performing the call.\n")
        else:
            with open(self.logfile, 'a') as flog:
                flog.write(
                    "Plugin hasn't initialized! Not performing the call.")

    def process_report(self):
        if (self.initialized):
            with open(self.logfile, 'a') as flog:
                flog.write("Creating report with violating licenses.\n")
            self.process_pkg_list()
            self.write_report_unwanted()
            with open(self.logfile, 'a') as flog:
                flog.write("Creating report in XML format.\n")
            self.write_report_xml()

    def process_pkg_list(self):
        if os.path.isfile (self.image_pkg_list):
            img_name = ""
            with open(self.image_pkg_list, 'r') as finput:
                for line in finput:
                    line = line.strip()
                    if not line:
                        continue
                    if line.startswith("Packages "):
                        img_name = line.split()[3]
                        with open(self.logfile, 'a') as flog:
                            flog.write("img_name: " + img_name + "\n")
                        continue
                    package_info = line.split()
                    pkg_name = package_info[0]
                    orig_pkg_name = package_info[2]
                    if (not self.image_pkgs) or ((pkg_name + " from " + img_name) not in self.image_pkgs):
                        self.image_pkgs.append(pkg_name + " from " + img_name + " " + orig_pkg_name)

    def write_report_xml(self):
        try:
            from lxml import etree
        except ImportError:
            try:
                import xml.etree.cElementTree as etree
            except ImportError:
                import xml.etree.ElementTree as etree
        num_tests = 0
        root = etree.Element('testsuite', name='LA_Plugin', tests='2')
        if os.path.isfile(self.report_name):
            with open(self.report_name, 'r') as f:
                class_name = "Non-approved-licenses"
                for line in f:
                    line = line.strip()
                    if line == "":
                        continue
                    if line.startswith("Packages that "):
                        class_name = "Violating-licenses"
                        continue
                    num_tests += 1
                    tcase1 = etree.SubElement(
                        root, 'testcase', classname=class_name, name=line.split(':', 1)[0])
                    etree.SubElement(
                        tcase1, 'failure', message=line, type='violation')
        else:
            tcase1 = etree.SubElement(
                root, 'testcase', classname='ISA_LAChecker', name='none')
            num_tests = 1
        root.set('tests', str(num_tests))
        tree = etree.ElementTree(root)
        output = self.report_name + '.xml'
        try:
            tree.write(output, encoding='UTF-8',
                       pretty_print=True, xml_declaration=True)
        except TypeError:
            tree.write(output, encoding='UTF-8', xml_declaration=True)

    def write_report_unwanted(self):
        if os.path.isfile(self.report_name + "_unwanted"):
            with open(self.logfile, 'a') as flog:
                flog.write("image_pkgs: " + str(self.image_pkgs) + "\n")
                flog.write("self.la_plugin_image_whitelist: " + str(self.la_plugin_image_whitelist) + "\n")
                flog.write("self.la_plugin_image_blacklist: " + str(self.la_plugin_image_blacklist) + "\n")
            with open(self.report_name, 'a') as fout:
                with open(self.report_name + "_unwanted", 'r') as f:
                    fout.write(
                        "\n\nPackages that violate mandatory license requirements:\n")
                    for line in f:
                        line = line.strip()
                        pkg_name = line.split(':',1)[0]
                        if (not self.image_pkgs):
                            fout.write(line + " from image name not available \n")
                            continue
                        for pkg_info in self.image_pkgs:
                            image_pkg_name = pkg_info.split()[0]
                            image_name = pkg_info.split()[2]
                            image_orig_pkg_name = pkg_info.split()[3]
                            if ((image_pkg_name == pkg_name) or (image_orig_pkg_name == pkg_name)):
                                if self.la_plugin_image_whitelist and (image_name not in self.la_plugin_image_whitelist):
                                    continue
                                if self.la_plugin_image_blacklist and (image_name in self.la_plugin_image_blacklist):
                                    continue
                                fout.write(line + " from image " + image_name)
                                if (image_pkg_name != image_orig_pkg_name):
                                    fout.write(" binary_pkg_name " + image_pkg_name + "\n")
                                    continue
                                fout.write("\n")
            os.remove(self.report_name + "_unwanted")

    def find_files(self, init_path):
        list_of_files = []
        for (dirpath, dirnames, filenames) in os.walk(init_path):
            for f in filenames:
                list_of_files.append(str(dirpath + "/" + f)[:])
        return list_of_files

    def check_license(self, license, file_path):
        with open(os.path.dirname(__file__) + file_path, 'r') as f:
            for line in f:
                s = line.rstrip()
                curr_license = license.split(':',1)[1]
                if s == curr_license:
                    return True
        return False

    def check_exceptions(self, pkg_name, license, file_path):
        with open(os.path.dirname(__file__) + file_path, 'r') as f:
            for line in f:
                s = line.rstrip()
                curr_license = license.split(':',1)[1]
                if s == pkg_name + " " + curr_license:
                    return True
        return False

# ======== supported callbacks from ISA ============= #

def init(ISA_config):
    global LicenseChecker
    LicenseChecker = ISA_LicenseChecker(ISA_config)


def getPluginName():
    return "ISA_LicenseChecker"


def process_package(ISA_pkg):
    global LicenseChecker
    return LicenseChecker.process_package(ISA_pkg)


def process_report():
    global LicenseChecker
    return LicenseChecker.process_report()

# ==================================================== #
