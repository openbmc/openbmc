#include <linux/module.h>
#include <linux/init.h>

int testfoo_value(void)
{
	return 42;
}
EXPORT_SYMBOL(testfoo_value);

static int __init testfoo_init(void)
{
	pr_info("testfoo loaded\n");
	return 0;
}

static void __exit testfoo_exit(void)
{
	pr_info("testfoo unloaded\n");
}

module_init(testfoo_init);
module_exit(testfoo_exit);
MODULE_LICENSE("GPL");
