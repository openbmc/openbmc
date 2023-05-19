# EVM & IMA keys

The following IMA & EVM debug/test keys are in this directory

- ima-local-ca.priv: The CA's private key (password: 1234)
- ima-local-ca.pem: The CA's self-signed certificate
- privkey_ima.pem: IMA & EVM private key used for signing files
- x509_ima.der: Certificate containing public key (of privkey_ima.pem) to verify signatures

The CA's (self-signed) certificate can be used to verify the validity of
the x509_ima.der certificate. Since the CA certificate will be built into
the Linux kernel, any key (x509_ima.der) loaded onto the .ima keyring must
pass this test:

```
  openssl verify -CAfile ima-local-ca.pem x509_ima.der
````
