# SPDX-License-Identifier: MIT
#
# Copyright Pengutronix <yocto@pengutronix.de>
#

# Support generating a dm-verity image and the parameters required to assemble
# the corresponding table for the device-mapper driver. The latter will be
# stored in the file ${DEPLOY_DIR_IMAGE}/<IMAGE_LINK_NAME>.verity-params. Note
# that in the resulting image the hash tree data is appended to the contents of
# the original image without an explicit superblock to keep things simple and
# compact.
#
# The above mentioned parameter file can be sourced by a shell to finally create
# the desired blockdevice via "dmsetup" (found in meta-oe's recipe
# "libdevmapper"), e.g.
#
#   . <IMAGE_LINK_NAME>.verity-params
#   dmsetup create <dm_dev_name> --readonly --table "0 $VERITY_DATA_SECTORS \
#       verity 1 <dev> <hash_dev> \
#       $VERITY_DATA_BLOCK_SIZE  $VERITY_HASH_BLOCK_SIZE \
#       $VERITY_DATA_BLOCKS  $VERITY_DATA_BLOCKS \
#       $VERITY_HASH_ALGORITHM  $VERITY_ROOT_HASH  $VERITY_SALT \
#       1 ignore_zero_blocks"
#
# As the hash tree data is found at the end of the image, <dev> and <hash_dev>
# should be the same blockdevice in the command shown above while <dm_dev_name>
# is the name of the to be created dm-verity-device.
#
# The root hash is calculated using a salt to make attacks more difficult. Thus,
# please grant each image recipe its own salt which could be generated e.g. via
#
#   dd if=/dev/random bs=1k count=1 | sha256sum
#
# and assign it to the parameter VERITY_SALT.

inherit image-artifact-names

do_image_verity[depends] += "cryptsetup-native:do_populate_sysroot"

CLASS_VERITY_SALT = "4e5f0d9b6ccac5e843598d4e4545046232b48451a399acb2106822b43679b375"
VERITY_SALT ?= "${CLASS_VERITY_SALT}"
VERITY_BLOCK_SIZE ?= "4096"
VERITY_IMAGE_FSTYPE ?= "ext4"
VERITY_IMAGE_SUFFIX ?= ".verity"
VERITY_INPUT_IMAGE ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${VERITY_IMAGE_FSTYPE}"

IMAGE_TYPEDEP:verity = "${VERITY_IMAGE_FSTYPE}"
IMAGE_TYPES_MASKED += "verity"

python __anonymous() {
    if 'verity' not in d.getVar('IMAGE_FSTYPES'):
        return

    dep_task = 'do_image_{}'.format(d.getVar('VERITY_IMAGE_FSTYPE').replace('-', '_'))
    bb.build.addtask('do_image_verity', 'do_image_complete', dep_task, d)
}

python do_image_verity () {
    import os
    import subprocess
    import shutil

    link = d.getVar('VERITY_INPUT_IMAGE')
    image = os.path.realpath(link)

    verity_image_suffix = d.getVar('VERITY_IMAGE_SUFFIX')
    verity = '{}{}'.format(image, verity_image_suffix)

    # For better readability the parameter VERITY_BLOCK_SIZE is specified in
    # bytes. It must be a multiple of the logical sector size which is 512 bytes
    # in Linux. Make sure that this is the case as otherwise the resulting
    # issues would be hard to debug later.
    block_size = int(d.getVar('VERITY_BLOCK_SIZE'))
    if block_size % 512 != 0:
        bb.fatal("VERITY_BLOCK_SIZE must be a multiple of 512!")

    salt = d.getVar('VERITY_SALT')
    if salt == d.getVar('CLASS_VERITY_SALT'):
        bb.warn("Please overwrite VERITY_SALT with an image specific one!")

    shutil.copyfile(image, verity)

    data_size_blocks, data_size_rest = divmod(os.stat(verity).st_size, block_size)
    data_blocks = data_size_blocks + (1 if data_size_rest else 0)
    data_size = data_blocks * block_size

    bb.debug(1, f"data_size_blocks: {data_size_blocks}, {data_size_rest}")
    bb.debug(1, f"data_size: {data_size}")

    # Create verity image
    try:
        output = subprocess.check_output([
            'veritysetup', 'format',
            '--no-superblock',
            '--salt={}'.format(salt),
            '--data-blocks={}'.format(data_blocks),
            '--data-block-size={}'.format(block_size),
            '--hash-block-size={}'.format(block_size),
            '--hash-offset={}'.format(data_size),
            verity, verity,
            ])
    except subprocess.CalledProcessError as err:
        bb.fatal('%s returned with %s (%s)' % (err.cmd, err.returncode, err.output))

    try:
        with open(image + '.verity-info', 'wb') as f:
            f.write(output)
    except Exception as err:
        bb.fatal('Unexpected error %s' % err)

    # Create verity params
    params = []
    for line in output.decode('ASCII').splitlines():
        if not ':' in line:
            continue
        k, v = line.split(':', 1)
        k = k.strip().upper().replace(' ', '_')
        v = v.strip()
        bb.debug(1, f"{k} {v}")
        params.append('VERITY_{}={}'.format(k, v))

    params.append('VERITY_DATA_SECTORS={}'.format(data_size//512))

    try:
        with open(image + '.verity-params', 'w') as f:
            f.write('\n'.join(params))
    except Exception as err:
        bb.fatal('Unexpected error %s' % err)

    # Create symlinks
    for suffix in [ verity_image_suffix, '.verity-info', '.verity-params' ]:
        try:
            os.remove(link + suffix)
        except FileNotFoundError:
            pass
        os.symlink(os.path.basename(image) + suffix, link + suffix)
}
