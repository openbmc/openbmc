#!/usr/bin/env python3
"""Loads a "target" YAML file and overwrites its values with values from
"override" YAML files.

Override files are processed in the order given.

Usage:
    merge_settings.py <target yaml> [override/remove yamls]
"""
import sys
import yaml
import copy

def dict_merge(target, source, remove):
    """Deep merge for dicts.

    Works like dict.update() that recursively updates/removes any dict values
    present in both parameters.

    Args:
        target (dict): Values to be overwritten by corresponding values from
            `source`.
        source (dict): Overriding values. Not changed by call.
        remove (bool): If this is true then it removes the entry provided in
            'source' along with all entries under it from 'target'. Otherwise
            it overrides the values from 'source'

    Returns:
        `target` with values overwritten/removed from those in `source` at any 
        and all levels of nested dicts.
    """
    if not isinstance(source, dict):
        return source
    for k, v in source.items():
        if k in target and isinstance(target[k], dict):
            dict_merge(target[k], v, remove)
        else:
            if remove is True and k in target:
                target.pop(k)
            else:
                target[k] = copy.deepcopy(v)
    return target

if len(sys.argv) < 2:
    sys.exit('Argument required: target yaml')

if len(sys.argv) == 2:
    # No overrides to handle
    sys.exit()

target_filename = sys.argv[1]
with open(target_filename) as target_file:
    data = yaml.safe_load(target_file)
    print('Loaded target YAML file ' + target_filename)

for override_filename in sys.argv[2:]:
    if override_filename.endswith('.override.yml'):
        with open(override_filename) as override_file:
            override = yaml.safe_load(override_file)
            dict_merge(data, override, False)
            print('Merged override YAML file ' + override_filename)
    elif override_filename.endswith('.remove.yml'):
        with open(override_filename) as override_file:
            override = yaml.safe_load(override_file)
            dict_merge(data, override, True)
            print('Removed data from source YAML file' + override_filename)

with open(target_filename, 'w') as target_file:
    yaml.dump(data, target_file)
    print('Wrote merged target YAML file ' + target_filename)
