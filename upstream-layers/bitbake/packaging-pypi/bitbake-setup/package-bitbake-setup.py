#!/usr/bin/env python3

import argparse
import logging
import shutil
from pathlib import Path


def create_packaging_workspace(directory):
    # Create the directory for packaging workspace
    if len(directory or "") > 0:
        workspace_dir = Path(directory)
    else:
        # This script is located in packaging-pypi/bitbake-setup/package-bitbake-setup.py
        workspace_dir = Path(__file__).parents[2] / "packaging-workspace"

    if not workspace_dir.exists():
        logging.debug(f"Created packaging workspace at: {workspace_dir}")
        workspace_dir.mkdir(exist_ok=True)
    else:
        logging.debug(f"Packaging workspace already exists at: {workspace_dir}")

    # Copy packaging files to the workspace
    files_to_copy = [
        "packaging-pypi/bitbake-setup/pyproject.toml",
        "packaging-pypi/bitbake-setup/README.md",
        "packaging-pypi/bitbake-setup/LICENSE",
        "LICENSE.MIT",
        "LICENSE.GPL-2.0-only"
    ]

    for file in files_to_copy:
        src_path = Path(__file__).parents[2] / file
        dest_path = workspace_dir / Path(file).name

        if src_path.is_dir():
            shutil.copytree(src_path, dest_path, dirs_exist_ok=True)
            logging.debug(f"Copied directory: {src_path} to {dest_path}")
        else:
            shutil.copy2(src_path, dest_path)
            logging.debug(f"Copied file: {src_path} to {dest_path}")


    # Copy necessary modules to the workspace
    modules_to_bundle = [
        "lib/bb",
    ]

    for module in modules_to_bundle:
        src_path = Path(__file__).parents[2] / module
        dest_path = workspace_dir / "src" / Path(module).name

        if src_path.is_dir():
            shutil.copytree(src_path, dest_path, dirs_exist_ok=True)
            logging.debug(f"Bundled module directory: {src_path} to {dest_path}")
        else:
            shutil.copy2(src_path, dest_path)
            logging.debug(f"Bundled module file: {src_path} to {dest_path}")

    # Create bitbake_setup module
    bitbake_setup_dir = Path(workspace_dir / "src" / "bitbake_setup")
    bitbake_setup_dir.mkdir(exist_ok=True)
    Path(bitbake_setup_dir / "__init__.py").touch()
    shutil.copy2(Path(__file__).parents[2] / "bin" / "bitbake-setup", str(bitbake_setup_dir / "__main__.py"))


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

    parser = argparse.ArgumentParser(description='Package bitbake-setup for PyPI')
    parser.add_argument('-v', '--verbose', action='store_true', help='increase output verbosity.')
    parser.add_argument('-d', '--directory', type=str, help='specify the directory to create the packaging workspace in.')

    args = parser.parse_args()

    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)

    create_packaging_workspace(args.directory)
