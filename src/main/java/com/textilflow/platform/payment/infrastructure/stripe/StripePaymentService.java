package com.textilflow.platform.payment.infrastructure.stripe;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Stripe payment service implementation
 * Handles direct integration with Stripe API for payment processing
 */
@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    /**
     * Initialize Stripe configuration after bean creation
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("Stripe initialized with secret key");
    }

    /**
     * Create payment intent for subscription upgrade
     * Returns client secret for frontend payment completion
     */
    public String createPaymentIntent(Long amountInCents, Long userId, String subscriptionPlan) {
        System.out.println("=== Creating Stripe Payment Intent ===");
        System.out.println("Amount in cents: " + amountInCents);
        System.out.println("Currency: USD");

        try {
            // Create metadata for tracking
            Map<String, String> metadata = new HashMap<>();
            metadata.put("user_id", userId.toString());
            metadata.put("subscription_plan", subscriptionPlan);
            metadata.put("context", "subscription_upgrade");

            // Build payment intent parameters
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .putAllMetadata(metadata)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            // Create payment intent via Stripe API
            PaymentIntent intent = PaymentIntent.create(params);

            System.out.println("Payment Intent created successfully");
            System.out.println("Payment Intent ID: " + intent.getId());
            System.out.println("Client Secret: " + intent.getClientSecret());

            return intent.getClientSecret();

        } catch (StripeException e) {
            System.err.println("Stripe error: " + e.getMessage());
            System.err.println("Error code: " + e.getCode());
            System.err.println("Error type: " + e.getCode());
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error creating payment intent: " + e.getMessage());
            throw new RuntimeException("Unexpected error during payment intent creation", e);
        }
    }

    /**
     * Retrieve payment intent details from Stripe
     * Used for verification and webhook processing
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            System.out.println("Retrieved Payment Intent: " + paymentIntentId);
            System.out.println("Status: " + intent.getStatus());
            return intent;
        } catch (StripeException e) {
            System.err.println("Error retrieving payment intent: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Verify webhook signature for security
     * Ensures webhook requests actually come from Stripe
     */
    public boolean verifyWebhookSignature(String payload, String sigHeader, String webhookSecret) {
        try {
            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(
                    payload, sigHeader, webhookSecret
            );
            System.out.println("Webhook signature verified successfully");
            System.out.println("Event type: " + event.getType());
            return true;
        } catch (Exception e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get payment intent metadata for processing
     * Extracts user and subscription information from Stripe metadata
     */
    public Map<String, String> getPaymentIntentMetadata(String paymentIntentId) {
        try {
            PaymentIntent intent = retrievePaymentIntent(paymentIntentId);
            Map<String, String> metadata = intent.getMetadata();

            System.out.println("Payment Intent metadata retrieved:");
            metadata.forEach((key, value) ->
                    System.out.println("  " + key + ": " + value)
            );

            return metadata;
        } catch (Exception e) {
            System.err.println("Error getting payment intent metadata: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Helper method to validate payment intent status
     * Returns true if payment was successful
     */
    public boolean isPaymentSuccessful(String paymentIntentId) {
        try {
            PaymentIntent intent = retrievePaymentIntent(paymentIntentId);
            boolean isSuccessful = "succeeded".equals(intent.getStatus());

            System.out.println("Payment status check for " + paymentIntentId + ": " +
                    (isSuccessful ? "SUCCESS" : "NOT_SUCCESS (" + intent.getStatus() + ")"));

            return isSuccessful;
        } catch (Exception e) {
            System.err.println("Error checking payment status: " + e.getMessage());
            return false;
        }
    }
}