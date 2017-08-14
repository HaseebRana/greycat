/**
 * Copyright 2017 DataThings - All rights reserved.
 */
package greycat.ac.auth;

import greycat.*;
import greycat.plugin.NodeState;
import greycat.struct.EStructArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gregory NAIN on 05/08/2017.
 */
public class OtpManager {

    private Graph _graph;
    private String _acIndexName;
    private boolean _strict;

    private String _issuer = "";

    private OtpEngine _engine = new OtpEngine();

    public OtpManager(Graph _graph, String acIndexName, String issuer, boolean strict) {
        this._graph = _graph;
        this._acIndexName = acIndexName;
        this._issuer = issuer;
        this._strict = strict;

    }

    public void getSecret(long uid, Callback<OtpSecret> value) {
        this.load(uid, value);
    }

    public void resetSecret(long uid, Callback<OtpSecret> newSecret) {
        OtpSecret secret = new OtpSecret(uid, OtpEngine.generateSecretKey());
        save(uid, secret, result -> {
            newSecret.on(secret);
        });
    }

    public void setSecret(long uid, OtpSecret newSecret, Callback<Boolean> done) {
        save(uid, newSecret, done);
    }

    public boolean isStrict() {
        return _strict;
    }

    public String getIssuer() {
        return _issuer;
    }

    public void deleteSecret(long uid, Callback<Boolean> done) {
        delete(uid, done);
    }

    public void verifyCredentials(long uid, Map<String, String> credentials, Callback<Long> callback) {
        load(uid, secret -> {
            String code = credentials.get("otp");
            if (secret != null && code != null && code.length() > 0 && code.length() < 10) {
                if (_engine.check_code(secret.secret(), code, System.currentTimeMillis())) {
                    callback.on(uid);
                } else {
                    callback.on(null);
                }
            } else {
                if (_strict) {
                    callback.on(null);
                } else {
                    callback.on(uid);
                }
            }
        });
    }

    private void load(long uid, Callback<OtpSecret> done) {
        _graph.index(-1, System.currentTimeMillis(), _acIndexName, acIndex -> {
            acIndex.findFrom(otpNodes -> {
                Node otpNode;
                if (otpNodes == null || otpNodes.length == 0) {
                    done.on(null);
                    return;
                } else {
                    otpNode = otpNodes[0];
                }
                ((Index) otpNode.getAt(0)).find(secretsNodes -> {
                    if (secretsNodes == null || secretsNodes.length == 0) {
                        done.on(null);
                    } else {
                        done.on(OtpSecret.load(secretsNodes[0]));
                    }
                }, acIndex.world(), acIndex.time(), "" + uid);
            }, "secrets");
        });

    }

    private void delete(long uid, Callback<Boolean> done) {
        _graph.index(-1, System.currentTimeMillis(), _acIndexName, acIndex -> {
            acIndex.findFrom(otpNodes -> {
                Node otpNode;
                if (otpNodes == null || otpNodes.length == 0) {
                    done.on(true);
                    return;
                } else {
                    otpNode = otpNodes[0];
                }
                Index localIndex = (Index) otpNode.getAt(0);
                localIndex.find(secretsNodes -> {
                    if (secretsNodes == null || secretsNodes.length == 0) {
                        done.on(true);
                    } else {
                        localIndex.unindex(secretsNodes[0]);
                        _graph.save((saved) -> {
                            secretsNodes[0].drop(result ->
                                    done.on(true));
                        });

                    }
                }, acIndex.world(), acIndex.time(), "" + uid);
            }, "secrets");
        });

    }

    private void save(long uid, OtpSecret secret, Callback<Boolean> done) {
        _graph.index(-1, System.currentTimeMillis(), _acIndexName, acIndex -> {
            acIndex.findFrom(otpNodes -> {
                Node otpNode;
                if (otpNodes == null || otpNodes.length == 0) {
                    otpNode = _graph.newNode(acIndex.world(), acIndex.time());
                    otpNode.setGroup(2);
                    otpNode.set("name", Type.STRING, "secrets");
                    ((Index) otpNode.getOrCreateAt(0, Type.INDEX)).declareAttributes(null, "uid");
                    acIndex.update(otpNode);
                } else {
                    otpNode = otpNodes[0];
                }
                Index otpIndex = (Index) otpNode.getAt(0);
                otpIndex.find(secretsNodes -> {

                    if (secretsNodes == null || secretsNodes.length == 0) {
                        _graph.lookup(0, acIndex.time(), uid, userNode -> {
                            Node secretNode = _graph.newNode(acIndex.world(), acIndex.time());
                            secretNode.setGroup(userNode.group());
                            secret.save(secretNode);
                            otpIndex.update(secretNode);
                            _graph.save(done);
                        });
                    } else {
                        secret.save(secretsNodes[0]);
                        _graph.save(done);
                    }

                }, acIndex.world(), acIndex.time(), "" + uid);

            }, "secrets");
        });
    }

}