#include <linux/module.h>
#include <linux/init.h>

extern int testfoo_value(void);

static int __init testbar_init(void)
{
	pr_info("testbar loaded, foo says %d\n", testfoo_value());
	return 0;
}

static void __exit testbar_exit(void)
{
	pr_info("testbar unloaded\n");
}

module_init(testbar_init);
module_exit(testbar_exit);
MODULE_LICENSE("GPL");
