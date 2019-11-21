pragma solidity ^0.5.8;

contract PublicKeys {

    mapping (string => string) publicKeyMap;

    function setKey(string memory did, string memory key) public {
        publicKeyMap[did] = key;
    }

    function getKey(string memory did) public view returns (string memory) {
        return publicKeyMap[did];
    }
}
