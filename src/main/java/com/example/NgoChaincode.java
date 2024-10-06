package com.example;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.List;
import java.util.logging.Logger;

public class NgoChaincode extends ChaincodeBase {

    private static final Logger logger = Logger.getLogger(NgoChaincode.class.getName());

    // Init method
    @Override
    public Response init(ChaincodeStub stub) {
        try {
            logger.info("Initializing NGO Chaincode...");
            System.out.println("Initialising ......");
            return newSuccessResponse("NGO Chaincode Initialized");
        } catch (Exception e) {
            return newErrorResponse("Initialization failed: " + e.getMessage());
        }
    }

    // Invoke method
    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String function = stub.getFunction();
            List<String> args = stub.getParameters();

            switch (function) {
                case "registerNGO":
                    return registerNGO(stub, args);
                case "createDonationRequest":
                    return createDonationRequest(stub, args);
                case "updateDonationRequest":
                    return updateDonationRequest(stub, args);
                case "deleteDonationRequest":
                    return deleteDonationRequest(stub, args);
                case "donate":
                    return donate(stub, args);
                case "queryNGO":
                    return queryNGO(stub, args);
                case "queryDonationRequest":
                    return queryDonationRequest(stub, args);
                default:
                    return newErrorResponse("Invalid function name: " + function);
            }
        } catch (Exception e) {
            return newErrorResponse("Invoke failed: " + e.getMessage());
        }
    }

    // Register NGO function
    private Response registerNGO(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Expected 2 arguments: NGO ID and NGO Information");
        }

        String ngoID = args.get(0);
        String ngoInfo = args.get(1);

        // Store the NGO information in the ledger
        stub.putStringState(ngoID, ngoInfo);

        return newSuccessResponse("NGO registered successfully: " + ngoID);
    }

    // Create Donation Request function
    private Response createDonationRequest(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            return newErrorResponse("Expected 3 arguments: Donation ID, NGO ID, and Donation Request Information");
        }

        String donationID = args.get(0);
        String ngoID = args.get(1);
        String donationRequestInfo = args.get(2);

        // Composite key for the donation request
        CompositeKey donationKey = stub.createCompositeKey("DonationRequest", ngoID, donationID);

        // Store the donation request in the ledger
        stub.putStringState(donationKey.toString(), donationRequestInfo);

        return newSuccessResponse("Donation request created successfully: " + donationID);
    }

    // Update Donation Request function
    private Response updateDonationRequest(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            return newErrorResponse("Expected 3 arguments: Donation ID, NGO ID, and Updated Donation Request Information");
        }

        String donationID = args.get(0);
        String ngoID = args.get(1);
        String updatedDonationRequestInfo = args.get(2);

        // Composite key for the donation request
        CompositeKey donationKey = stub.createCompositeKey("DonationRequest", ngoID, donationID);

        // Check if the donation request exists
        String existingInfo = stub.getStringState(donationKey.toString());
        if (existingInfo == null || existingInfo.isEmpty()) {
            return newErrorResponse("Donation request not found: " + donationID);
        }

        // Update the donation request in the ledger
        stub.putStringState(donationKey.toString(), updatedDonationRequestInfo);

        return newSuccessResponse("Donation request updated successfully: " + donationID);
    }

    // Delete Donation Request function
    private Response deleteDonationRequest(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Expected 2 arguments: NGO ID and Donation ID");
        }

        String ngoID = args.get(0);
        String donationID = args.get(1);

        // Composite key for the donation request
        CompositeKey donationKey = stub.createCompositeKey("DonationRequest", ngoID, donationID);

        // Check if the donation request exists
        String existingInfo = stub.getStringState(donationKey.toString());
        if (existingInfo == null || existingInfo.isEmpty()) {
            return newErrorResponse("Donation request not found: " + donationID);
        }

        // Delete the donation request from the ledger
        stub.delState(donationKey.toString());

        return newSuccessResponse("Donation request deleted successfully: " + donationID);
    }

    // Donate function
    private Response donate(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3) {
            return newErrorResponse("Expected 3 arguments: Donation ID, NGO ID, and Donation Amount");
        }

        String donationID = args.get(0);
        String ngoID = args.get(1);
        String donationAmount = args.get(2);

        // Composite key for the donation
        CompositeKey donationKey = stub.createCompositeKey("Donation", ngoID, donationID);

        // Store the donation amount in the ledger
        stub.putStringState(donationKey.toString(), donationAmount);

        return newSuccessResponse("Donation made successfully: " + donationID);
    }

    // Query NGO function
    private Response queryNGO(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Expected 1 argument: NGO ID");
        }

        String ngoID = args.get(0);

        // Retrieve NGO information from the ledger
        String ngoInfo = stub.getStringState(ngoID);

        if (ngoInfo == null || ngoInfo.isEmpty()) {
            return newErrorResponse("NGO not found: " + ngoID);
        }

        return newSuccessResponse(ngoInfo);
    }

    // Query Donation Request function
    private Response queryDonationRequest(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Expected 2 arguments: NGO ID and Donation ID");
        }

        String ngoID = args.get(0);
        String donationID = args.get(1);

        // Composite key for the donation request
        CompositeKey donationKey = stub.createCompositeKey("DonationRequest", ngoID, donationID);

        // Retrieve the donation request information from the ledger
        String donationRequestInfo = stub.getStringState(donationKey.toString());

        if (donationRequestInfo == null || donationRequestInfo.isEmpty()) {
            return newErrorResponse("Donation request not found: " + donationID);
        }

        return newSuccessResponse(donationRequestInfo);
    }

    // Main method to start the chaincode
    public static void main(String[] args) {
        logger.info("Main........");
        System.out.println("Main Initialising ......");
        new NgoChaincode().start(args);
    }
}
