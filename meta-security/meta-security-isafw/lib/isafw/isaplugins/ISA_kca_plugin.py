#
# ISA_kca_plugin.py - Kernel config options analyzer plugin, part of ISA FW
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

try:
    from lxml import etree
except ImportError:
    try:
        import xml.etree.cElementTree as etree
    except ImportError:
        import xml.etree.ElementTree as etree
import importlib

KCAnalyzer = None


class ISA_KernelChecker():
    initialized = False

    def __init__(self, ISA_config):
        self.logfile = ISA_config.logdir + "/isafw_kcalog"
        self.full_report_name = ISA_config.reportdir + "/kca_full_report_" + \
            ISA_config.machine + "_" + ISA_config.timestamp
        self.problems_report_name = ISA_config.reportdir + \
            "/kca_problems_report_" + ISA_config.machine + "_" + ISA_config.timestamp
        self.full_reports = ISA_config.full_reports
        self.initialized = True
        self.arch = ISA_config.arch
        with open(self.logfile, 'w') as flog:
            flog.write("\nPlugin ISA_KernelChecker initialized!\n")

    def append_recommendation(self, report, key, value):
        report.write("Recommended value:\n")
        report.write(key + ' : ' + str(value) + '\n')
        comment = self.comments.get(key, '')
        if comment != '':
            report.write("Comment:\n")
            report.write(comment + '\n')

    def process_kernel(self, ISA_kernel):
        if (self.initialized):
            if (ISA_kernel.img_name and ISA_kernel.path_to_config):
                # Merging common and arch configs
                common_config_module = importlib.import_module('isafw.isaplugins.configs.kca.{}'.format('common'))
                arch_config_module = importlib.import_module('isafw.isaplugins.configs.kca.{}'.format(self.arch))

                for c in ["hardening_kco", "keys_kco", "security_kco", "integrity_kco",
                          "hardening_kco_ref", "keys_kco_ref", "security_kco_ref", "integrity_kco_ref",
                          "comments"]:
                    setattr(self, c, merge_config(getattr(arch_config_module, c), getattr(common_config_module, c)))
                with open(self.logfile, 'a') as flog:
                    flog.write("Analyzing kernel config file at: " + ISA_kernel.path_to_config +
                               " for the image: " + ISA_kernel.img_name + "\n")
                with open(ISA_kernel.path_to_config, 'r') as fkernel_conf:
                    for line in fkernel_conf:
                        line = line.strip('\n')
                        for key in self.hardening_kco:
                            if key + '=' in line:
                                self.hardening_kco[key] = line.split('=')[1]
                        for key in self.keys_kco:
                            if key + '=' in line:
                                self.keys_kco[key] = line.split('=')[1]
                        for key in self.security_kco:
                            if key + '=' in line:
                                self.security_kco[key] = line.split('=')[1]
                        for key in self.integrity_kco:
                            if key + '=' in line:
                                self.integrity_kco[key] = line.split('=')[1]
                with open(self.logfile, 'a') as flog:
                    flog.write("\n\nhardening_kco values: " +
                               str(self.hardening_kco))
                    flog.write("\n\nkeys_kco values: " + str(self.keys_kco))
                    flog.write("\n\nsecurity_kco values: " +
                               str(self.security_kco))
                    flog.write("\n\nintegrity_kco values: " +
                               str(self.integrity_kco))
                self.write_full_report(ISA_kernel)
                self.write_problems_report(ISA_kernel)

            else:
                with open(self.logfile, 'a') as flog:
                    flog.write(
                        "Mandatory arguments such as image name and path to config are not provided!\n")
                    flog.write("Not performing the call.\n")
        else:
            with open(self.logfile, 'a') as flog:
                flog.write(
                    "Plugin hasn't initialized! Not performing the call!\n")

    def write_full_report(self, ISA_kernel):
        if self.full_reports:
            with open(self.full_report_name + "_" + ISA_kernel.img_name, 'w') as freport:
                freport.write("Report for image: " +
                              ISA_kernel.img_name + '\n')
                freport.write("With the kernel conf at: " +
                              ISA_kernel.path_to_config + '\n\n')
                freport.write("Hardening options:\n")
                for key in sorted(self.hardening_kco):
                    freport.write(
                        key + ' : ' + str(self.hardening_kco[key]) + '\n')
                freport.write("\nKey-related options:\n")
                for key in sorted(self.keys_kco):
                    freport.write(key + ' : ' + str(self.keys_kco[key]) + '\n')
                freport.write("\nSecurity options:\n")
                for key in sorted(self.security_kco):
                    freport.write(
                        key + ' : ' + str(self.security_kco[key]) + '\n')
                freport.write("\nIntegrity options:\n")
                for key in sorted(self.integrity_kco):
                    freport.write(
                        key + ' : ' + str(self.integrity_kco[key]) + '\n')

    def write_problems_report(self, ISA_kernel):
        self.write_text_problems_report(ISA_kernel)
        self.write_xml_problems_report(ISA_kernel)

    def write_text_problems_report(self, ISA_kernel):
        with open(self.problems_report_name + "_" + ISA_kernel.img_name, 'w') as freport:
            freport.write("Report for image: " + ISA_kernel.img_name + '\n')
            freport.write("With the kernel conf at: " +
                          ISA_kernel.path_to_config + '\n\n')
            freport.write("Hardening options that need improvement:\n")
            for key in sorted(self.hardening_kco):
                if (self.hardening_kco[key] != self.hardening_kco_ref[key]):
                    valid = False
                    if (key == "CONFIG_CMDLINE"):
                        if (len(self.hardening_kco['CONFIG_CMDLINE']) > 0):
                            valid = True
                    if (key == "CONFIG_DEBUG_STRICT_USER_COPY_CHECKS"):
                        if (self.hardening_kco['CONFIG_ARCH_HAS_DEBUG_STRICT_USER_COPY_CHECKS'] == 'y'):
                            valid = True
                    if (key == "CONFIG_RANDOMIZE_BASE_MAX_OFFSET"):
                        options = self.hardening_kco_ref[key].split(',')
                        for option in options:
                            if (option == self.hardening_kco[key]):
                                valid = True
                                break
                    if not valid:
                        freport.write("\nActual value:\n")
                        freport.write(
                            key + ' : ' + str(self.hardening_kco[key]) + '\n')
                        self.append_recommendation(freport, key, self.hardening_kco_ref[key])
            freport.write("\nKey-related options that need improvement:\n")
            for key in sorted(self.keys_kco):
                if (self.keys_kco[key] != self.keys_kco_ref[key]):
                    freport.write("\nActual value:\n")
                    freport.write(key + ' : ' + str(self.keys_kco[key]) + '\n')
                    self.append_recommendation(freport, key, self.keys_kco_ref[key])
            freport.write("\nSecurity options that need improvement:\n")
            for key in sorted(self.security_kco):
                if (self.security_kco[key] != self.security_kco_ref[key]):
                    valid = False
                    if (key == "CONFIG_DEFAULT_SECURITY"):
                        options = self.security_kco_ref[key].split(',')
                        for option in options:
                            if (option == self.security_kco[key]):
                                valid = True
                                break
                    if ((key == "CONFIG_SECURITY_SELINUX") or
                            (key == "CONFIG_SECURITY_SMACK") or
                            (key == "CONFIG_SECURITY_APPARMOR") or
                            (key == "CONFIG_SECURITY_TOMOYO")):
                        if ((self.security_kco['CONFIG_SECURITY_SELINUX'] == 'y') or
                                (self.security_kco['CONFIG_SECURITY_SMACK'] == 'y') or
                                (self.security_kco['CONFIG_SECURITY_APPARMOR'] == 'y') or
                                (self.security_kco['CONFIG_SECURITY_TOMOYO'] == 'y')):
                            valid = True
                    if not valid:
                        freport.write("\nActual value:\n")
                        freport.write(
                            key + ' : ' + str(self.security_kco[key]) + '\n')
                        self.append_recommendation(freport, key, self.security_kco_ref[key])
            freport.write("\nIntegrity options that need improvement:\n")
            for key in sorted(self.integrity_kco):
                if (self.integrity_kco[key] != self.integrity_kco_ref[key]):
                    valid = False
                    if ((key == "CONFIG_IMA_DEFAULT_HASH_SHA1") or
                            (key == "CONFIG_IMA_DEFAULT_HASH_SHA256") or
                            (key == "CONFIG_IMA_DEFAULT_HASH_SHA512") or
                            (key == "CONFIG_IMA_DEFAULT_HASH_WP512")):
                        if ((self.integrity_kco['CONFIG_IMA_DEFAULT_HASH_SHA256'] == 'y') or
                                (self.integrity_kco['CONFIG_IMA_DEFAULT_HASH_SHA512'] == 'y')):
                            valid = True
                    if not valid:
                        freport.write("\nActual value:\n")
                        freport.write(
                            key + ' : ' + str(self.integrity_kco[key]) + '\n')
                        self.append_recommendation(freport, key, self.integrity_kco_ref[key])

    def write_xml_problems_report(self, ISA_kernel):
        # write_problems_report_xml
        num_tests = len(self.hardening_kco) + len(self.keys_kco) + \
            len(self.security_kco) + len(self.integrity_kco)
        root = etree.Element(
            'testsuite', name='KCA_Plugin', tests=str(num_tests))
        for key in sorted(self.hardening_kco):
            tcase1 = etree.SubElement(
                root, 'testcase', classname='Hardening options', name=key)
            if (self.hardening_kco[key] != self.hardening_kco_ref[key]):
                valid = False
                if (key == "CONFIG_CMDLINE"):
                    if (len(self.hardening_kco['CONFIG_CMDLINE']) > 0):
                        valid = True
                if (key == "CONFIG_DEBUG_STRICT_USER_COPY_CHECKS"):
                    if (self.hardening_kco['CONFIG_ARCH_HAS_DEBUG_STRICT_USER_COPY_CHECKS'] == 'y'):
                        valid = True
                if (key == "CONFIG_RANDOMIZE_BASE_MAX_OFFSET"):
                    options = self.hardening_kco_ref[key].split(',')
                    for option in options:
                        if (option == self.hardening_kco[key]):
                            valid = True
                            break
                if not valid:
                    msg1 = 'current=' + key + ' is ' + \
                        str(self.hardening_kco[
                            key]) + ', recommended=' + key + ' is ' + str(self.hardening_kco_ref[key])
                    etree.SubElement(
                        tcase1, 'failure', message=msg1, type='violation')
        for key in sorted(self.keys_kco):
            tcase2 = etree.SubElement(
                root, 'testcase', classname='Key-related options', name=key)
            if (self.keys_kco[key] != self.keys_kco_ref[key]):
                msg2 = 'current=' + key + ' is ' + \
                    str(self.keys_kco[key] + ', recommended=' +
                        key + ' is ' + str(self.keys_kco_ref[key]))
                etree.SubElement(
                    tcase2, 'failure', message=msg2, type='violation')
        for key in sorted(self.security_kco):
            tcase3 = etree.SubElement(
                root, 'testcase', classname='Security options', name=key)
            if (self.security_kco[key] != self.security_kco_ref[key]):
                valid = False
                if (key == "CONFIG_DEFAULT_SECURITY"):
                    options = self.security_kco_ref[key].split(',')
                    for option in options:
                        if (option == self.security_kco[key]):
                            valid = True
                            break
                if ((key == "CONFIG_SECURITY_SELINUX") or
                        (key == "CONFIG_SECURITY_SMACK") or
                        (key == "CONFIG_SECURITY_APPARMOR") or
                        (key == "CONFIG_SECURITY_TOMOYO")):
                    if ((self.security_kco['CONFIG_SECURITY_SELINUX'] == 'y') or
                            (self.security_kco['CONFIG_SECURITY_SMACK'] == 'y') or
                            (self.security_kco['CONFIG_SECURITY_APPARMOR'] == 'y') or
                            (self.security_kco['CONFIG_SECURITY_TOMOYO'] == 'y')):
                        valid = True
                if not valid:
                    msg3 = 'current=' + key + ' is ' + \
                        str(self.security_kco[key]) + ', recommended=' + \
                        key + ' is ' + str(self.security_kco_ref[key])
                    etree.SubElement(
                        tcase3, 'failure', message=msg3, type='violation')
        for key in sorted(self.integrity_kco):
            tcase4 = etree.SubElement(
                root, 'testcase', classname='Integrity options', name=key)
            if (self.integrity_kco[key] != self.integrity_kco_ref[key]):
                valid = False
                if ((key == "CONFIG_IMA_DEFAULT_HASH_SHA1") or
                        (key == "CONFIG_IMA_DEFAULT_HASH_SHA256") or
                        (key == "CONFIG_IMA_DEFAULT_HASH_SHA512") or
                        (key == "CONFIG_IMA_DEFAULT_HASH_WP512")):
                    if ((self.integrity_kco['CONFIG_IMA_DEFAULT_HASH_SHA256'] == 'y') or
                            (self.integrity_kco['CONFIG_IMA_DEFAULT_HASH_SHA512'] == 'y')):
                        valid = True
                if not valid:
                    msg4 = 'current=' + key + ' is ' + \
                        str(self.integrity_kco[
                            key]) + ', recommended=' + key + ' is ' + str(self.integrity_kco_ref[key])
                    etree.SubElement(
                        tcase4, 'failure', message=msg4, type='violation')
        tree = etree.ElementTree(root)
        output = self.problems_report_name + "_" + ISA_kernel.img_name + '.xml'
        try:
            tree.write(output, encoding='UTF-8',
                       pretty_print=True, xml_declaration=True)
        except TypeError:
            tree.write(output, encoding='UTF-8', xml_declaration=True)


def merge_config(arch_kco, common_kco):
    merged = arch_kco.copy()
    merged.update(common_kco)
    return merged

# ======== supported callbacks from ISA ============= #
def init(ISA_config):
    global KCAnalyzer
    KCAnalyzer = ISA_KernelChecker(ISA_config)


def getPluginName():
    return "ISA_KernelChecker"


def process_kernel(ISA_kernel):
    global KCAnalyzer
    return KCAnalyzer.process_kernel(ISA_kernel)
# ==================================================== #
