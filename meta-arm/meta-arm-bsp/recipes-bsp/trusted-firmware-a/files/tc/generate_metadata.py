#!/usr/bin/env python3
# Copyright (c) 2021, Arm Limited. All rights reserved.
#
# SPDX-License-Identifier: BSD-3-Clause

import argparse
import uuid
import zlib

def main(metadata_file, img_type_uuids, location_uuids, img_uuids):
    def add_field_to_metadata(value):
        # Write the integer values to file in little endian representation
        with open(metadata_file, "ab") as fp:
            fp.write(value.to_bytes(4, byteorder='little'))

    def add_uuid_to_metadata(uuid_str):
        # Validate UUID string and write to file in little endian representation
        uuid_val = uuid.UUID(uuid_str)
        with open(metadata_file, "ab") as fp:
            fp.write(uuid_val.bytes_le)

    # Fill metadata preamble
    add_field_to_metadata(1) #version=1
    add_field_to_metadata(0) #active_index=0
    add_field_to_metadata(0) #previous_active_index=0

    for img_type_uuid, location_uuid in zip(img_type_uuids, location_uuids):
        # Fill metadata image entry
        add_uuid_to_metadata(img_type_uuid) # img_type_uuid
        add_uuid_to_metadata(location_uuid) # location_uuid

        for img_uuid in img_uuids:
            # Fill metadata bank image info
            add_uuid_to_metadata(img_uuid) # image unique bank_uuid
            add_field_to_metadata(1)       # accepted=1
            add_field_to_metadata(0)       # reserved (MBZ)

    # Prepend CRC32
    with open(metadata_file, 'rb+') as fp:
        content = fp.read()
        crc = zlib.crc32(content)
        fp.seek(0)
        fp.write(crc.to_bytes(4, byteorder='little') + content)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--metadata_file', required=True,
                        help='Output binary file to store the metadata')
    parser.add_argument('--img_type_uuids', type=str, nargs='+', required=True,
                        help='A list of UUIDs identifying the image types')
    parser.add_argument('--location_uuids', type=str, nargs='+', required=True,
                        help='A list of UUIDs of the storage volumes where the images are located. '
                             'Must have the same length as img_type_uuids.')
    parser.add_argument('--img_uuids', type=str, nargs='+', required=True,
                        help='A list UUIDs of the images in a firmware bank')

    args = parser.parse_args()

    if len(args.img_type_uuids) != len(args.location_uuids):
        parser.print_help()
        raise argparse.ArgumentError(None, 'Arguments img_type_uuids and location_uuids must have the same length.')

    main(args.metadata_file, args.img_type_uuids, args.location_uuids, args.img_uuids)
