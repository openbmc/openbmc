# Extract UUID from ${ROOTFS}, which must have been built
# by the time that this function gets called. Only works
# on ext file systems and depends on tune2fs.
def get_rootfs_uuid(d):
    import subprocess
    rootfs = d.getVar('ROOTFS', True)
    output = subprocess.check_output(['tune2fs', '-l', rootfs])
    for line in output.split('\n'):
        if line.startswith('Filesystem UUID:'):
            uuid = line.split()[-1]
            bb.note('UUID of %s: %s' % (rootfs, uuid))
            return uuid
    bb.fatal('Could not determine filesystem UUID of %s' % rootfs)

# Replace the special <<uuid-of-rootfs>> inside a string (like the
# root= APPEND string in a syslinux.cfg or gummiboot entry) with the
# actual UUID of the rootfs. Does nothing if the special string
# is not used.
def replace_rootfs_uuid(d, string):
    UUID_PLACEHOLDER = '<<uuid-of-rootfs>>'
    if UUID_PLACEHOLDER in string:
        uuid = get_rootfs_uuid(d)
        string = string.replace(UUID_PLACEHOLDER, uuid)
    return string
