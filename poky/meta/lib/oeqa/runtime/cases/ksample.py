import os
import time

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oeid import OETestID
from oeqa.core.decorator.data import skipIfNotFeature

# need some kernel fragments
# echo "KERNEL_FEATURES_append += \" features\/kernel\-sample\/kernel\-sample.scc\"" >> local.conf
class KSample(OERuntimeTestCase):
    def cmd_and_check(self, cmd='', match_string=''):
        status, output = self.target.run(cmd)
        if not match_string:
            # send cmd
            msg = '%s failed, %s' % (cmd, output)
            self.assertEqual(status, 0, msg=msg)
        else:
            # check result
            result = ("%s" % match_string) in output
            msg = output
            self.assertTrue(result, msg)
            self.assertEqual(status, 0, cmd)

    def check_config(self, config_opt=''):
        cmd = "zcat /proc/config.gz | grep %s" % config_opt
        status, output = self.target.run(cmd)
        result = ("%s=y" % config_opt) in output
        if not result:
            self.skipTest("%s is not set" % config_opt)

    def check_module_exist(self, path='', module_name=''):
        status, output = self.target.run("uname -r")
        cmd = "ls " + "/lib/modules/" + output + "/kernel/samples/" + path + module_name
        status, output = self.target.run(cmd)
        if status != 0:
            error_info = module_name + " doesn't exist"
            self.skipTest(error_info)

    def kfifo_func(self, name=''):
        module_prename = name + "-example"
        module_name = name + "-example.ko"
        sysmbol_name = name + "_example"

        # make sure if module exists
        self.check_module_exist("kfifo/", module_name)
        # modprobe
        self.cmd_and_check("modprobe %s" % module_prename)
        # lsmod
        self.cmd_and_check("lsmod | grep %s | cut -d\' \' -f1" % sysmbol_name, sysmbol_name)
        # check result
        self.cmd_and_check("dmesg | grep \"test passed\" ", "test passed")
        # rmmod
        self.cmd_and_check("rmmod %s" %  module_prename)

    def kprobe_func(self, name=''):
        # check config
        self.check_config("CONFIG_KPROBES")

        module_prename = name + "_example"
        module_name = name + "_example.ko"
        sysmbol_name = module_prename

        # make sure if module exists
        self.check_module_exist("kprobes/", module_name)
        # modprobe
        self.cmd_and_check("modprobe %s" % module_prename)
        # lsmod
        self.cmd_and_check("lsmod | grep %s | cut -d\' \' -f1" % sysmbol_name, sysmbol_name)
        # check result
        self.cmd_and_check("dmesg | grep Planted | head -n10", "Planted")
        # rmmod
        self.cmd_and_check("rmmod %s" % module_prename)

    def kobject_func(self, name=''):
        module_prename = name + "_example"
        module_name = name + "-example.ko"
        sysmbol_name = module_prename

        # make sure if module exists
        self.check_module_exist("kobject/", module_name)
        # modprobe
        self.cmd_and_check("modprobe %s" % module_prename)
        # lsmod
        self.cmd_and_check("lsmod | grep %s | cut -d\' \' -f1" % sysmbol_name, sysmbol_name)
        # check result
        self.cmd_and_check("ls /sys/kernel/%s/" % sysmbol_name, "bar")
        # rmmod
        self.cmd_and_check("rmmod %s" % module_prename)

class KSampleTest(KSample):
    # kfifo
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_kfifo_test(self):
        index = ["dma", "bytestream", "inttype", "record"]
        for i in index:
            self.kfifo_func(i)

    # kprobe
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_kprobe_test(self):
        index = ["kprobe", "kretprobe"]
        for i in index:
            self.kprobe_func(i)

    # kobject
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_kobject_test(self):
        index = ["kobject", "kset"]
        for i in index:
            self.kobject_func(i)

    #trace
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_trace_events(self):
        # check config
        self.check_config("CONFIG_TRACING_SUPPORT")
        # make sure if module exists
        self.check_module_exist("trace_events/", "trace-events-sample.ko")
        # modprobe
        self.cmd_and_check("modprobe trace-events-sample")
        # lsmod
        self.cmd_and_check("lsmod | grep trace_events_sample | cut -d\' \' -f1", "trace_events_sample")
        # check dir
        self.cmd_and_check("ls /sys/kernel/debug/tracing/events/ | grep sample-trace", "sample-trace")
        # enable trace
        self.cmd_and_check("echo 1 > /sys/kernel/debug/tracing/events/sample-trace/enable")
        self.cmd_and_check("cat /sys/kernel/debug/tracing/events/sample-trace/enable")
        # check result
        status = 1
        count = 0
        while status != 0:
            time.sleep(1)
            status, output = self.target.run('cat /sys/kernel/debug/tracing/trace | grep hello | head -n1 | cut -d\':\' -f2')
            if " foo_bar" in output:
                break
            count = count + 1
            if count > 5:
                self.assertTrue(False, "Time out when check result")
        # disable trace
        self.cmd_and_check("echo 0 > /sys/kernel/debug/tracing/events/sample-trace/enable")
        # clean up trace
        self.cmd_and_check("echo > /sys/kernel/debug/tracing/trace")
        # rmmod
        self.cmd_and_check("rmmod trace-events-sample")

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_trace_printk(self):
        # check config
        self.check_config("CONFIG_TRACING_SUPPORT")
        # make sure if module exists
        self.check_module_exist("trace_printk/", "trace-printk.ko")
        # modprobe
        self.cmd_and_check("modprobe trace-printk")
        # lsmod
        self.cmd_and_check("lsmod | grep trace_printk | cut -d\' \' -f1", "trace_printk")
        # check result
        self.cmd_and_check("cat /sys/kernel/debug/tracing/trace | grep trace_printk_irq_work | head -n1 | cut -d\':\' -f2", " trace_printk_irq_work")
        # clean up trace
        self.cmd_and_check("echo > /sys/kernel/debug/tracing/trace")
        # rmmod
        self.cmd_and_check("rmmod trace-printk")

    # hw breakpoint
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_hw_breakpoint_example(self):
        # check arch
        status, output = self.target.run("uname -m")
        result = ("x86" in output) or ("aarch64" in output)
        if not result:
            self.skipTest("the arch doesn't support hw breakpoint" % output)
        # check config
        self.check_config("CONFIG_KALLSYMS_ALL")
        # make sure if module exists
        self.check_module_exist("hw_breakpoint/", "data_breakpoint.ko")
        # modprobe
        self.cmd_and_check("modprobe data_breakpoint")
        # lsmod
        self.cmd_and_check("lsmod | grep data_breakpoint | cut -d\' \' -f1", "data_breakpoint")
        # check result
        self.cmd_and_check("cat /var/log/messages | grep sample_hbp_handler", "sample_hbp_handler")
        # rmmod
        self.cmd_and_check("rmmod data_breakpoint")

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_configfs_sample(self):
        # check config
        status, ret = self.target.run('zcat /proc/config.gz | grep CONFIG_CONFIGFS_FS')
        if not ["CONFIG_CONFIGFS_FS=m" in ret or "CONFIG_CONFIGFS_FS=y" in ret]:
            self.skipTest("CONFIG error")
        # make sure if module exists
        self.check_module_exist("configfs/", "configfs_sample.ko")
        # modprobe
        self.cmd_and_check("modprobe configfs_sample")
        # lsmod
        self.cmd_and_check("lsmod | grep configfs_sample | cut -d\' \' -f1 | head -n1", "configfs_sample")

        status = 1
        count = 0
        while status != 0:
            time.sleep(1)
            status, ret = self.target.run('cat /sys/kernel/config/01-childless/description')
            count = count + 1
            if count > 200:
                self.skipTest("Time out for check dir")

        # rmmod
        self.cmd_and_check("rmmod configfs_sample")

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    def test_cn_test(self):
        # make sure if module exists
        self.check_module_exist("connector/", "cn_test.ko")
        # modprobe
        self.cmd_and_check("modprobe cn_test")
        # lsmod
        self.cmd_and_check("lsmod | grep cn_test | cut -d\' \' -f1", "cn_test")
        # check result
        self.cmd_and_check("cat /proc/net/connector | grep cn_test | head -n1 | cut -d\' \' -f1", "cn_test")
        # rmmod
        self.cmd_and_check("rmmod cn_test")
