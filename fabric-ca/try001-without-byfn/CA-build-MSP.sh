export CURRENT_TRY_PATH=/home/yong/Documents/Fabric-study20190202001/fabric-ca/try001-without-byfn
cd $CURRENT_TRY_PATH
mkdir server-guo
cd server-guo
fabric-ca-server start -b guo:1992 --cfg.affiliations.allowremove  --cfg.identities.allowremove &
sleep 15
cd ..
echo start fabric ca server success----------------------------------------

mkdir client-guo
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo
fabric-ca-client enroll -u http://guo:1992@localhost:7054
fabric-ca-client affiliation list
fabric-ca-client affiliation add com
fabric-ca-client affiliation add com.example
fabric-ca-client affiliation add com.example.org1
fabric-ca-client affiliation add com.example.org2
fabric-ca-client affiliation list
echo created orgs on server----------------------------------------

mkdir client-guo-msps
cd client-guo-msps
mkdir -p ./example.com/msp
fabric-ca-client getcacert -M $CURRENT_TRY_PATH/client-guo-msps/example.com/msp
mkdir -p ./org1.example.com/msp
fabric-ca-client getcacert -M $CURRENT_TRY_PATH/client-guo-msps/org1.example.com/msp
mkdir -p ./org2.example.com/msp
fabric-ca-client getcacert -M $CURRENT_TRY_PATH/client-guo-msps/org2.example.com/msp
echo created msp path For all orgs----------------------------------------

PWD=$(fabric-ca-client register --id.name Admin@example.com \
                            --id.type client \
                            --id.affiliation "com.example" \
                            --id.attrs '"hf.Registrar.Roles=client,orderer,peer,user","hf.Registrar.DelegateRoles=client,orderer,peer,user",hf.Registrar.Attributes=*,hf.GenCRL=true,hf.Revoker=true,hf.AffiliationMgr=true,hf.IntermediateCA=true,role=admin:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir -p ./example.com/admin
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/example.com/admin
echo http://Admin@example.com:$PWD@localhost:7054

fabric-ca-client enroll -u http://Admin@example.com:$PWD@localhost:7054
fabric-ca-client affiliation list
mkdir -p ./example.com/msp/admincerts/
cp ./example.com/admin/msp/signcerts/cert.pem ./example.com/msp/admincerts/
echo got msp For Admin@example.com----------------------------------------

PWD=$(fabric-ca-client register --id.name Admin@org1.example.com \
                            --id.type client \
                            --id.affiliation "com.example.org1" \
                            --id.maxenrollments 0 \
                            --id.attrs '"hf.Registrar.Roles=client,orderer,peer,user","hf.Registrar.DelegateRoles=client,orderer,peer,user",hf.Registrar.Attributes=*,hf.GenCRL=true,hf.Revoker=true,hf.AffiliationMgr=true,hf.IntermediateCA=true,role=admin:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir -p ./org1.example.com/admin
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org1.example.com/admin
fabric-ca-client enroll -u http://Admin@org1.example.com:$PWD@localhost:7054
mkdir -p ./org1.example.com/msp/admincerts/
cp ./org1.example.com/admin/msp/signcerts/cert.pem ./org1.example.com/msp/admincerts/
echo got msp For Admin@org1.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/example.com/admin
PWD=$(fabric-ca-client register --id.name Admin@org2.example.com \
                            --id.type client \
                            --id.affiliation "com.example.org2" \
                            --id.maxenrollments 0 \
                            --id.attrs '"hf.Registrar.Roles=client,orderer,peer,user","hf.Registrar.DelegateRoles=client,orderer,peer,user",hf.Registrar.Attributes=*,hf.GenCRL=true,hf.Revoker=true,hf.AffiliationMgr=true,hf.IntermediateCA=true,role=admin:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir -p ./org2.example.com/admin
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org2.example.com/admin
ls ./org2.example.com/admin
fabric-ca-client enroll -u http://Admin@org2.example.com:$PWD@localhost:7054
ls ./org2.example.com/admin
mkdir -p ./org2.example.com/msp/admincerts/
cp ./org2.example.com/admin/msp/signcerts/cert.pem ./org2.example.com/msp/admincerts/
echo got msp For Admin@org2.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/example.com/admin
PWD=$(fabric-ca-client register --id.name orderer.example.com \
                            --id.type orderer \
                            --id.affiliation "com.example" \
                            --id.maxenrollments 0 \
                            --id.attrs 'role=orderer:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir -p $CURRENT_TRY_PATH/client-guo-msps/example.com/orderer
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/example.com/orderer
fabric-ca-client enroll -u http://orderer.example.com:$PWD@localhost:7054
mkdir $CURRENT_TRY_PATH/client-guo-msps/example.com/orderer/msp/admincerts
cp ./example.com/admin/msp/signcerts/cert.pem ./example.com/orderer/msp/admincerts/
echo got msp For orderer.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org1.example.com/admin
PWD=$(fabric-ca-client register --id.name peer0.org1.example.com \
                            --id.type peer \
                            --id.affiliation "com.example.org1" \
                            --id.maxenrollments 0 \
                            --id.attrs 'role=peer:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir ./org1.example.com/peer0
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org1.example.com/peer0
fabric-ca-client enroll -u http://peer0.org1.example.com:$PWD@localhost:7054
mkdir ./org1.example.com/peer0/msp/admincerts
cp ./org1.example.com/admin/msp/signcerts/cert.pem ./org1.example.com/peer0/msp/admincerts/
echo got msp For peer0.org1.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org1.example.com/admin
PWD=$(fabric-ca-client register --id.name peer1.org1.example.com \
                            --id.type peer \
                            --id.affiliation "com.example.org1" \
                            --id.maxenrollments 0 \
                            --id.attrs 'role=peer:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir ./org1.example.com/peer1
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org1.example.com/peer1
fabric-ca-client enroll -u http://peer1.org1.example.com:$PWD@localhost:7054
mkdir ./org1.example.com/peer1/msp/admincerts
cp ./org1.example.com/admin/msp/signcerts/cert.pem ./org1.example.com/peer1/msp/admincerts/
echo got msp For peer1.org1.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org2.example.com/admin
PWD=$(fabric-ca-client register --id.name peer0.org2.example.com \
                            --id.type peer \
                            --id.affiliation "com.example.org2" \
                            --id.maxenrollments 0 \
                            --id.attrs 'role=peer:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir ./org2.example.com/peer0
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org2.example.com/peer0
fabric-ca-client enroll -u http://peer0.org2.example.com:$PWD@localhost:7054
mkdir ./org2.example.com/peer0/msp/admincerts
cp ./org2.example.com/admin/msp/signcerts/cert.pem ./org2.example.com/peer0/msp/admincerts/
echo got msp For peer0.org2.example.com----------------------------------------

export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org2.example.com/admin
PWD=$(fabric-ca-client register --id.name peer1.org2.example.com \
                            --id.type peer \
                            --id.affiliation "com.example.org2" \
                            --id.maxenrollments 0 \
                            --id.attrs 'role=peer:ecert'
)
PWD=${PWD##*: }
echo $PWD
mkdir ./org2.example.com/peer1
export FABRIC_CA_CLIENT_HOME=$CURRENT_TRY_PATH/client-guo-msps/org2.example.com/peer1
fabric-ca-client enroll -u http://peer1.org2.example.com:$PWD@localhost:7054
mkdir ./org2.example.com/peer1/msp/admincerts
cp ./org2.example.com/admin/msp/signcerts/cert.pem ./org2.example.com/peer1/msp/admincerts/
echo got msp For peer1.org2.example.com----------------------------------------




