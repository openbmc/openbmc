inherit linuxloader

GLIBC_GETLOADER = "${@get_linuxloader(d)}"

def glibc_dl_info(d):
    infos = {'ldconfig':set(), 'lddrewrite':set()}

    loaders = all_multilib_tune_values(d, "GLIBC_GETLOADER").split()
    for loader in loaders:
        infos['ldconfig'].add('{"' + loader + '",' + "FLAG_ELF_LIBC6" + ' }')
        infos['lddrewrite'].add(loader)

    infos['ldconfig'] = ','.join(sorted(infos['ldconfig']))
    infos['lddrewrite'] = ' '.join(sorted(infos['lddrewrite']))
    return infos

EGLIBC_KNOWN_INTERPRETER_NAMES = "${@glibc_dl_info(d)['ldconfig']}"
RTLDLIST = "${@glibc_dl_info(d)['lddrewrite']}"
RTLDLIST_class-nativesdk = "${base_libdir}/${@bb.utils.contains('SDK_ARCH', 'x86_64', 'ld-linux-x86-64.so.2', 'ld-linux.so.2', d)}"
glibc_dl_info[vardepsexclude] = "OVERRIDES"
