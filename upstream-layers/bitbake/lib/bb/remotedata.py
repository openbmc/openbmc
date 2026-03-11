"""
BitBake 'remotedata' module

Provides support for using a datastore from the bitbake client
"""

# Copyright (C) 2016  Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb.data

class RemoteDatastores:
    """Used on the server side to manage references to server-side datastores"""
    def __init__(self, cooker):
        self.cooker = cooker
        self.datastores = {}
        self.locked = []
        self.datastores[0] = self.cooker.data
        self.nextindex = 1

    def __len__(self):
        return len(self.datastores)

    def __getitem__(self, key):
        # Cooker could have changed its datastore from under us
        self.datastores[0] = self.cooker.data
        return self.datastores[key]

    def items(self):
        return self.datastores.items()

    def store(self, d, locked=False):
        """
        Put a datastore into the collection. If locked=True then the datastore
        is understood to be managed externally and cannot be released by calling
        release().
        """
        idx = self.nextindex
        self.datastores[idx] = d
        if locked:
            self.locked.append(idx)
        self.nextindex += 1
        return idx

    def check_store(self, d, locked=False):
        """
        Put a datastore into the collection if it's not already in there;
        in either case return the index
        """
        for key, val in self.datastores.items():
            if val is d:
                idx = key
                break
        else:
            idx = self.store(d, locked)
        return idx

    def release(self, idx):
        """Discard a datastore in the collection"""
        if idx in self.locked:
            raise Exception('Tried to release locked datastore %d' % idx)
        del self.datastores[idx]

