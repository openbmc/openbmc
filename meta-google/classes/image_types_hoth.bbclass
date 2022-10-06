# The offsets of the partitions that change when Hoth is enabled
# From the device tree, in kB
FLASH_IMAGE_DESC_OFFSET:hoth = "${@960 if FLASH_SIZE == '65536' else 7232}"
FLASH_HOTH_UPDATE_OFFSET:hoth = "${@1024 if FLASH_SIZE == '65536' else 31744}"
FLASH_HOTH_MAILBOX_OFFSET:hoth = "${@65472 if FLASH_SIZE == '65536' else 7168}"
unset FLASH_UBOOT_ENV_OFFSET

# 64 bit kernels are larger, so they require a different layout
FLASH_IMAGE_DESC_OFFSET:hoth:aarch64 = "${@61312 if FLASH_SIZE == '65536' else 7232}"
FLASH_HOTH_UPDATE_OFFSET:hoth:aarch64 = "${@61376 if FLASH_SIZE == '65536' else 31744}"

python do_generate_static:append() {
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                               'image-hoth-update'),
                  int(d.getVar('FLASH_HOTH_UPDATE_OFFSET', True)),
                  int(d.getVar('FLASH_SIZE', True)))
}
do_generate_static[depends] += "virtual/hoth-firmware:do_deploy"

python do_generate_layout () {
    import time
    import json

    def convertPart(name, startKb, endKb, static=False, wp=False, persist=False):
        regionTypes = []
        extraAttrs = {}
        if static:
            regionTypes.append('STATIC')
        if wp:
            regionTypes.append('WRITE_PROTECTED')
        if persist:
            regionTypes.append('PERSISTENT')
        if name == 'hoth_mailbox':
            regionTypes.append('MAILBOX')
            extraAttrs['mailbox_length'] = 1024

        start = int(startKb) * 1024
        end = int(endKb) * 1024

        return {
                'name': name,
                'offset': start,
                'length': end - start,
                'region_type': regionTypes,
                **extraAttrs,
        }

    # TODO: make this work for Aspeed too
    region = [
            convertPart(
                    'u_boot',
                    d.getVar('FLASH_UBOOT_OFFSET'),
                    d.getVar('FLASH_KERNEL_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'kernel',
                    d.getVar('FLASH_KERNEL_OFFSET'),
                    d.getVar('FLASH_ROFS_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'rofs',
                    d.getVar('FLASH_ROFS_OFFSET'),
                    d.getVar('FLASH_IMAGE_DESC_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'image_descriptor',
                    d.getVar('FLASH_IMAGE_DESC_OFFSET'),
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'hoth_update',
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    d.getVar('FLASH_RWFS_OFFSET')),
            convertPart(
                    'rwfs',
                    d.getVar('FLASH_RWFS_OFFSET'),
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    persist=True),
            convertPart(
                    'hoth_mailbox',
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    d.getVar('FLASH_SIZE')),
    ] if d.getVar('TARGET_ARCH') == "aarch64" else [
            convertPart(
                    'u_boot',
                    d.getVar('FLASH_UBOOT_OFFSET'),
                    d.getVar('FLASH_IMAGE_DESC_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'image_descriptor',
                    d.getVar('FLASH_IMAGE_DESC_OFFSET'),
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'hoth_update',
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    d.getVar('FLASH_KERNEL_OFFSET')),
            convertPart(
                    'kernel',
                    d.getVar('FLASH_KERNEL_OFFSET'),
                    d.getVar('FLASH_ROFS_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'rofs',
                    d.getVar('FLASH_ROFS_OFFSET'),
                    d.getVar('FLASH_RWFS_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'rwfs',
                    d.getVar('FLASH_RWFS_OFFSET'),
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    persist=True),
            convertPart(
                    'hoth_mailbox',
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    d.getVar('FLASH_SIZE')),
    ] if d.getVar('FLASH_SIZE') == '65536' else [
            convertPart(
                    'u_boot',
                    d.getVar('FLASH_UBOOT_OFFSET'),
                    d.getVar('FLASH_KERNEL_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'kernel',
                    d.getVar('FLASH_KERNEL_OFFSET'),
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'hoth_mailbox',
                    d.getVar('FLASH_HOTH_MAILBOX_OFFSET'),
                    d.getVar('FLASH_IMAGE_DESC_OFFSET')),
            convertPart(
                    'image_descriptor',
                    d.getVar('FLASH_IMAGE_DESC_OFFSET'),
                    d.getVar('FLASH_ROFS_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'rofs',
                    d.getVar('FLASH_ROFS_OFFSET'),
                    d.getVar('FLASH_RWFS_OFFSET'),
                    static=True,
                    wp=True),
            convertPart(
                    'rwfs',
                    d.getVar('FLASH_RWFS_OFFSET'),
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    persist=True),
            convertPart(
                    'hoth_update',
                    d.getVar('FLASH_HOTH_UPDATE_OFFSET'),
                    d.getVar('FLASH_SIZE')),
    ]

    machine = d.getVar('MACHINE')
    platform = d.getVar('PLATFORM')
    name = '{} {} image'.format(machine, d.getVar('DISTRO'))
    version = d.getVar('GBMC_VERSION').split('.')

    if not platform:
        raise NameError('PLATFORM not found, unable to generate layout, stopping build')

    layout = {
            'name': name,
            'major': int(version[0]),
            'minor': int(version[1]),
            'point': int(version[2]),
            'subpoint': int(version[3]),
            'platform': platform,
            'flash_capacity': int(d.getVar('FLASH_SIZE')) * 1024,
            'build_timestamp': int(time.time()),
            'region': region,
    }

    dir = d.getVar('DEPLOY_DIR_IMAGE')
    os.makedirs(dir, exist_ok=True)
    path = os.path.join(dir, 'cr51-image-layout.json')
    with open(path, 'w') as f:
        json.dump(layout, f, sort_keys=True, indent=4)
}

addtask generate_layout before do_image_complete
