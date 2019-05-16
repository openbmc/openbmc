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
        self.nextindex = 1

    def __len__(self):
        return len(self.datastores)

    def __getitem__(self, key):
        if key is None:
            return self.cooker.data
        else:
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

    def receive_datastore(self, remote_data):
        """Receive a datastore object sent from the client (as prepared by transmit_datastore())"""
        dct = dict(remote_data)
        d = bb.data_smart.DataSmart()
        d.dict = dct
        while True:
            if '_remote_data' in dct:
                dsindex = dct['_remote_data']['_content']
                del dct['_remote_data']
                if dsindex is None:
                    dct['_data'] = self.cooker.data.dict
                else:
                    dct['_data'] = self.datastores[dsindex].dict
                break
            elif '_data' in dct:
                idct = dict(dct['_data'])
                dct['_data'] = idct
                dct = idct
            else:
                break
        return d

    @staticmethod
    def transmit_datastore(d):
        """Prepare a datastore object for sending over IPC from the client end"""
        # FIXME content might be a dict, need to turn that into a list as well
        def copy_dicts(dct):
            if '_remote_data' in dct:
                dsindex = dct['_remote_data']['_content'].dsindex
                newdct = dct.copy()
                newdct['_remote_data'] = {'_content': dsindex}
                return list(newdct.items())
            elif '_data' in dct:
                newdct = dct.copy()
                newdata = copy_dicts(dct['_data'])
                if newdata:
                    newdct['_data'] = newdata
                return list(newdct.items())
            return None
        main_dict = copy_dicts(d.dict)
        return main_dict
