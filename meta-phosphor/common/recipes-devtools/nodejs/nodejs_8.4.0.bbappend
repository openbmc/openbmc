# Overriding map_nodejs_arch() is needed to support building nodejs on ppc64le
# We can remove this, once OpenBMC moves to a version of Yocto with
# http://lists.openembedded.org/pipermail/openembedded-devel/2018-February/116737.html
def map_nodejs_arch(a, d):
        import re

        if   re.match('i.86$', a): return 'ia32'
        elif re.match('x86_64$', a): return 'x64'
        elif re.match('aarch64$', a): return 'arm64'
        elif re.match('(powerpc64|ppc64le)$', a): return 'ppc64'
        elif re.match('powerpc$', a): return 'ppc'
        return a
