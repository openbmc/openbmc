#!/usr/bin/env python3
"""Copied from phosphor-settings-manager
Loads a "target" YAML file and overwrites its values with values from
"override" YAML files.

Override files are processed in the order given.

Usage:
    merge_settings.py <target yaml> [override yamls]
"""
import sys
import yaml
import copy

# Custom representer for None types. This is to handle empty dictionaries.
# By default Pyyaml outputs these as "null", whereas we want an empty character.
def represent_none(self, _):
    return self.represent_scalar('tag:yaml.org,2002:null', '')

def dict_merge(target, source):
    """Deep merge for dicts.

    Works like dict.update() that recursively updates any dict values present in
    both parameters.

    Args:
        target (dict): Values to be overwritten by corresponding values from
            `source`.
        source (dict): Overriding values. Not changed by call.

    Returns:
        `target` with values overwritten from those in `source` at any and all
        levels of nested dicts.
    """
    if not isinstance(source, dict):
        return source
    for k, v in source.items():
        if k in target and isinstance(target[k], dict):
            dict_merge(target[k], v)
        else:
            target[k] = copy.deepcopy(v)
    return target

if len(sys.argv) < 2:
    sys.exit('Argument required: target yaml')

if len(sys.argv) == 2:
    # No overrides to handle
    sys.exit(0)

yaml.add_representer(type(None), represent_none)

target_filename = sys.argv[1]
with open(target_filename) as target_file:
    data = yaml.safe_load(target_file)
    print('Loaded target YAML file ' + target_filename)

for override_filename in sys.argv[2:]:
    with open(override_filename) as override_file:
        override = yaml.safe_load(override_file)
        dict_merge(data, override)
        print('Merged override YAML file ' + override_filename)

with open(target_filename, 'w') as target_file:
    yaml.dump(data, target_file, default_flow_style=False)
    print('Wrote merged target YAML file ' + target_filename)
