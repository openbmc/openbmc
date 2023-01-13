def eat_run(d, cmd, *args):
    import bb.process
    import subprocess

    topdir = d.getVar('TOPDIR', True)
    toolchain_path = d.getVar('EXTERNAL_TOOLCHAIN', True)
    if not toolchain_path:
        return 'UNKNOWN', 'UNKNOWN'

    target_prefix = d.getVar('TARGET_PREFIX', True)
    path = os.path.join(toolchain_path, 'bin', target_prefix + cmd)
    args = [path] + list(args)

    return bb.process.run(args, cwd=topdir, stderr=subprocess.PIPE)

def eat_get_version(d):
    try:
        stdout, stderr = eat_run(d, 'gcc', '-v')
    except bb.process.CmdError as exc:
        bb.error('Failed to obtain external Arm toolchain version: %s' % exc)
        return 'UNKNOWN'
    else:
        last_line = stderr.splitlines()[-1]
        return last_line

# Extract the YYYY.MM or release version
def eat_get_main_version(d):
    version = eat_get_version(d)
    bb.debug(2, 'Trying for parse version info from: %s' % version)
    if version != 'UNKNOWN':
        if version.split()[4] == '(Arm':
            # gcc version 11.3.1 20220712 (Arm GNU Toolchain 11.3.Rel1)
            return version.split()[7].split(')')[0]
        elif version.split()[4] == '(GNU':
            # gcc version 9.2.1 20191025 (GNU Toolchain for the A-profile Architecture 9.2-2019.12 (arm-9.10))
            # gcc version 8.2.1 20180802 (GNU Toolchain for the A-profile Architecture 8.2-2018.11 (arm-rel-8.26))
            return version.split()[10].split('-')[1]
        elif version.split()[3] == '(GNU':
            # gcc version 8.3.0 (GNU Toolchain for the A-profile Architecture 8.3-2019.03 (arm-rel-8.36))
            return version.split()[9].split('-')[1]
        else:
            bb.error('Failed to parse external Arm toolchain version from: %s' % version)
    else:
        return version

# Extract the x.y.z version from 'gcc version 4.9.1'
def eat_get_gcc_version(d):
    version = eat_get_version(d)
    if version != 'UNKNOWN':
        return version.split()[2]
    else:
        return version

def eat_get_libc_version(d):
    import os,bb
    import subprocess

    syspath = bb.data.expand('${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}', d)
    if not syspath:
        return 'UNKNOWN'

    topdir = d.getVar('TOPDIR', True)
    lddpath = syspath + '/libc/usr/bin/ldd'

    if os.path.exists(lddpath):
        cmd = '/bin/sh ' + lddpath + ' --version'
        try:
            stdout, stderr = bb.process.run(cmd, cwd=topdir, stderr=subprocess.PIPE)
        except bb.process.CmdError as exc:
            bb.error('Failed to obtain external Arm libc version: %s' % exc)
            return 'UNKNOWN'
        else:
            first_line = stdout.splitlines()[0]
            return first_line.split()[2]

    return 'UNKNOWN'

def eat_get_kernel_version(d):
    import os,bb
    syspath = bb.data.expand('${EXTERNAL_TOOLCHAIN}/${EAT_TARGET_SYS}', d)
    if not syspath:
        return 'UNKNOWN'

    vf = syspath + '/libc/usr/include/linux/version.h'

    try:
        f = open(vf, 'r')
    except (OSError, IOError):
        return 'UNKNOWN'

    l = f.readlines();
    f.close();
    for s in l:
        if s.find('LINUX_VERSION_CODE') > 0:
            ver = int(s.split()[2])
            maj = ver / 65536
            ver = ver % 65536
            min = ver / 256
            ver = ver % 256
            return str(maj)+'.'+str(min)+'.'+str(ver)
    return 'UNKNOWN'

def eat_get_gdb_version(d):
    try:
        stdout, stderr = eat_run(d, 'gdb', '-v')
    except bb.process.CmdError:
        return 'UNKNOWN'
    else:
        first_line = stdout.splitlines()[0]
        return first_line.split()[-1]

def eat_get_bfd_version(d):
    try:
        stdout, stderr = eat_run(d, 'as', '--version')
    except bb.process.CmdError:
        return 'UNKNOWN'
    else:
        first_line = stdout.splitlines()[0]
        return first_line.split()[-1]

python external_arm_toolchain_version_handler () {
    if not isinstance(e, bb.event.ConfigParsed):
        return
    d = e.data
    ld = d.createCopy()

    d.setVar('EAT_VER_MAIN', eat_get_main_version(ld))
    d.setVar('EAT_VER_GCC', eat_get_gcc_version(ld))
    d.setVar('EAT_VER_LIBC', eat_get_libc_version(ld))
    d.setVar('EAT_VER_KERNEL', eat_get_kernel_version(ld))
    d.setVar('EAT_VER_GDB', eat_get_gdb_version(ld))
    d.setVar('EAT_VER_BFD', eat_get_bfd_version(ld))
}
addhandler external_arm_toolchain_version_handler
