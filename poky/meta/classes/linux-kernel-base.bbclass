# parse kernel ABI version out of <linux/version.h>
def get_kernelversion_headers(p):
    import re

    fn = p + '/include/linux/utsrelease.h'
    if not os.path.isfile(fn):
        # after 2.6.33-rc1
        fn = p + '/include/generated/utsrelease.h'
    if not os.path.isfile(fn):
        fn = p + '/include/linux/version.h'

    try:
        f = open(fn, 'r')
    except IOError:
        return None

    l = f.readlines()
    f.close()
    r = re.compile("#define UTS_RELEASE \"(.*)\"")
    for s in l:
        m = r.match(s)
        if m:
            return m.group(1)
    return None


def get_kernelversion_file(p):
    fn = p + '/kernel-abiversion'

    try:
        with open(fn, 'r') as f:
            return f.readlines()[0].strip()
    except IOError:
        return None

def linux_module_packages(s, d):
    suffix = ""
    return " ".join(map(lambda s: "kernel-module-%s%s" % (s.lower().replace('_', '-').replace('@', '+'), suffix), s.split()))

# that's all

