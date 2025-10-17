package br.com.fiap.rsa;

import java.math.BigInteger;

public class KeyPairGeneratorRSA {
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;
    private final BigInteger phi;
    private final BigInteger e;
    private final BigInteger d;

    public KeyPairGeneratorRSA() {
        this.p = BigInteger.valueOf(19L);
        this.q = BigInteger.valueOf(31L);
        this.n = p.multiply(q);
        this.phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = BigInteger.valueOf(7L);
        this.d = e.modInverse(phi);
    }

    public RSAKey generateKeyPair() {
        return new RSAKey(e, d, n);
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getPhi() {
        return phi;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }
}
