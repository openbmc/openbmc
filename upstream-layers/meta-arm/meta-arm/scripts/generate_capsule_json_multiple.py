# SPDX-FileCopyrightText: <text>Copyright 2025 Arm Limited and/or its
# affiliates <open-source-office@arm.com></text>
#
# SPDX-License-Identifier: MIT

"""
Capsule Payload JSON Generator

This script creates a JSON file that defines multiple capsule payloads.
Each payload is constructed using command-line input and includes key
metadata like firmware version, GUID, hardware instance, and more.

Usage:
    python generate_capsule_json_multiple.py \
        --fw_versions 1 1 2 \
        --guids guid1 guid2 guid3 \
        --hardware_instances 1 1 1 \
        --lowest_supported_versions 0 0 0 \
        --monotonic_counts 1 1 1 \
        --payloads bl2.bin initramfs.bin tfm_s.bin \
        --update_image_indexes 1 4 2 \
        --private_keys key.key key.key key.key \
        --certificates cert.crt cert.crt cert.crt \
        --components bl2 initramfs tfm_s \
        --selected_components bl2 \
        --output capsule_generation_config.json
"""

import json
import argparse
from typing import List


def parse_arguments() -> argparse.Namespace:
    """Parses command-line arguments."""
    parser = argparse.ArgumentParser(
        description="Generate a JSON file for multiple Capsule Payloads."
    )

    parser.add_argument(
        "--selected_components", default=[], nargs="*", required=False,
        help=(
            "Filters the payloads to include only those for the selected "
            "components (e.g., bl2, initramfs)."
            "All components are included when not specified."
        )
    )

    parser.add_argument(
        "--output", default="capsule_payloads.json", help="Output JSON file name"
    )

    # Required arguments for each payload entry
    required_args = {
        "components": "List of components",
        "fw_versions": "List of firmware versions",
        "guids": "List of GUIDs",
        "hardware_instances": "List of hardware instances",
        "lowest_supported_versions": "List of lowest supported firmware versions",
        "monotonic_counts": "List of monotonic counts",
        "payloads": "List of payload file paths",
        "update_image_indexes": "List of update image indexes",
        "private_keys": "List of private key file paths",
        "certificates": "List of certificate file paths",
    }

    for arg, desc in required_args.items():
        parser.add_argument(f"--{arg}", nargs="+", required=True, help=desc)

    return parser.parse_args()


def validate_input_lengths(args: argparse.Namespace) -> None:
    """Ensures all required input lists have the same length (excluding output and selected_components)."""
    list_lengths = {
        attr: len(getattr(args, attr))
        for attr in vars(args)
        if attr not in {"output", "selected_components"}  # Ignore optional fields
    }

    for attr, length in list_lengths.items():
        if length == 0:
            raise ValueError(f"Input list '{attr}' cannot be empty!")

    if len(set(list_lengths.values())) != 1:
        raise ValueError("All input lists must have the same length!")

def create_payloads(args: argparse.Namespace) -> List[dict]:
    """Generates the list of payload dictionaries to include in the final JSON."""
    num_payloads = len(args.components)
    selected_payloads = []

    for i in range(num_payloads):

        # If filtering is enabled, skip if not in the allowed components list
        if  args.components[i] not in args.selected_components:
            continue

        payload = {
            "Component": args.components[i],
            "FwVersion": args.fw_versions[i],
            "Guid": args.guids[i],
            "HardwareInstance": args.hardware_instances[i],
            "LowestSupportedVersion": args.lowest_supported_versions[i],
            "MonotonicCount": args.monotonic_counts[i],
            "Payload": args.payloads[i],
            "UpdateImageIndex": args.update_image_indexes[i],
            "OpenSslSignerPrivateCertFile": args.private_keys[i],
            "OpenSslTrustedPublicCertFile": args.certificates[i],
            "OpenSslOtherPublicCertFile": args.certificates[i],
        }

        selected_payloads.append(payload)

    if not selected_payloads:
        raise ValueError("None of the provided components match the selected_components list!")

    return selected_payloads


def write_json_file(output_path: str, payloads: List[dict]) -> None:
    """Writes the list of payloads to a JSON file with basic error handling."""
    try:
        with open(output_path, "w", encoding="utf-8") as file:
            json.dump({"Payloads": payloads}, file, indent=4)
        print(f"JSON file created: {output_path}")
    except (OSError) as e:
        print(f"Failed to write JSON file to {output_path}: {e}")
    except TypeError as e:
        print(f"Invalid data format in payloads: {e}")


def main() -> None:
    """Main script entry point."""
    args = parse_arguments()
    validate_input_lengths(args)
    payloads = create_payloads(args)
    write_json_file(args.output, payloads)


if __name__ == "__main__":
    main()
