/* SPDX-License-Identifier: GPL-2.0-only
 * A simple test kernel module with sysfs interface for devtool ide-sdk testing
 *
 * Usage:
 *   cat /sys/kernel/selftest_kmodule/magic
 *   Hello from selftest-kmodule
 */

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/kobject.h>
#include <linux/sysfs.h>

MODULE_LICENSE("GPL v2");
MODULE_AUTHOR("OpenEmbedded Contributors");
MODULE_DESCRIPTION("A simple test kernel module with sysfs interface for devtool ide-sdk testing");

/* Change this string to verify the modify/rebuild/redeploy workflow */
#define SELFTEST_MAGIC_STRING "Hello from selftest-kmodule"

static struct kobject *selftest_kobj;

static ssize_t magic_show(struct kobject *kobj, struct kobj_attribute *attr,
			  char *buf)
{
	return sysfs_emit(buf, "%s\n", SELFTEST_MAGIC_STRING);
}

static struct kobj_attribute magic_attr = __ATTR_RO(magic);

static int __init selftest_kmodule_init(void)
{
	int ret;

	selftest_kobj = kobject_create_and_add("selftest_kmodule", kernel_kobj);
	if (!selftest_kobj)
		return -ENOMEM;

	ret = sysfs_create_file(selftest_kobj, &magic_attr.attr);
	if (ret)
		kobject_put(selftest_kobj);

	pr_info("selftest-kmodule: loaded\n");
	return ret;
}

static void __exit selftest_kmodule_exit(void)
{
	sysfs_remove_file(selftest_kobj, &magic_attr.attr);
	kobject_put(selftest_kobj);
	pr_info("selftest-kmodule: unloaded\n");
}

module_init(selftest_kmodule_init);
module_exit(selftest_kmodule_exit);
