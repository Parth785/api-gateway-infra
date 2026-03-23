package com.example.notification_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.notification_service.dto.OrderCreatedEvent;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmation(OrderCreatedEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(event.getUserEmail());
            helper.setSubject("Order Confirmed #" + event.getOrderId() + " — TheStoreHub");
            helper.setFrom("TheStoreHub <" + fromEmail + ">");


            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
                
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f5;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                
                          <!-- Header -->
                          <tr>
                            <td style="background-color:#09090b;padding:32px 40px;text-align:center;">
                              <h1 style="margin:0;color:#ffffff;font-size:24px;font-weight:500;letter-spacing:-0.5px;">
                                arc<span style="color:#a78bfa;">.</span>store
                              </h1>
                            </td>
                          </tr>
                
                          <!-- Success banner -->
                          <tr>
                            <td style="background-color:#7c3aed;padding:24px 40px;text-align:center;">
                              <p style="margin:0;color:#ffffff;font-size:13px;letter-spacing:3px;text-transform:uppercase;opacity:0.8;">
                                Order Confirmed
                              </p>
                              <h2 style="margin:8px 0 0;color:#ffffff;font-size:32px;font-weight:600;">
                                Thank you, %s!
                              </h2>
                            </td>
                          </tr>
                
                          <!-- Body -->
                          <tr>
                            <td style="padding:40px;">
                
                              <p style="margin:0 0 24px;color:#52525b;font-size:15px;line-height:1.7;">
                                We have received your order and it is being processed. 
                                You will receive another update when your order is shipped.
                              </p>
                
                              <!-- Order details box -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fafafa;border:1px solid #e4e4e7;border-radius:12px;overflow:hidden;margin-bottom:32px;">
                                <tr>
                                  <td style="padding:20px 24px;border-bottom:1px solid #e4e4e7;">
                                    <p style="margin:0;color:#71717a;font-size:11px;letter-spacing:2px;text-transform:uppercase;">
                                      Order Details
                                    </p>
                                  </td>
                                </tr>
                                <tr>
                                  <td style="padding:20px 24px;">
                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                      <tr>
                                        <td style="padding:8px 0;color:#71717a;font-size:14px;">Order ID</td>
                                        <td style="padding:8px 0;color:#09090b;font-size:14px;font-weight:600;text-align:right;">#%d</td>
                                      </tr>
                                      <tr>
                                        <td style="padding:8px 0;color:#71717a;font-size:14px;border-top:1px solid #f4f4f5;">Status</td>
                                        <td style="padding:8px 0;text-align:right;border-top:1px solid #f4f4f5;">
                                          <span style="background-color:#fef9c3;color:#854d0e;font-size:12px;padding:4px 10px;border-radius:20px;font-weight:500;">
                                            PENDING
                                          </span>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style="padding:8px 0;color:#71717a;font-size:14px;border-top:1px solid #f4f4f5;">Total Amount</td>
                                        <td style="padding:8px 0;color:#7c3aed;font-size:18px;font-weight:700;text-align:right;border-top:1px solid #f4f4f5;">
                                          $%.2f
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                
                              <!-- What happens next -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                                <tr>
                                  <td style="padding-bottom:16px;">
                                    <p style="margin:0;color:#09090b;font-size:13px;letter-spacing:2px;text-transform:uppercase;font-weight:600;">
                                      What happens next
                                    </p>
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                      <tr>
                                        <td style="padding:12px 0;border-bottom:1px solid #f4f4f5;">
                                          <table cellpadding="0" cellspacing="0">
                                            <tr>
                                              <td style="width:32px;height:32px;background-color:#f3e8ff;border-radius:50%%;text-align:center;vertical-align:middle;">
                                                <span style="color:#7c3aed;font-size:14px;font-weight:600;">1</span>
                                              </td>
                                              <td style="padding-left:12px;">
                                                <p style="margin:0;color:#09090b;font-size:14px;font-weight:500;">Order Processing</p>
                                                <p style="margin:4px 0 0;color:#71717a;font-size:13px;">We are preparing your order</p>
                                              </td>
                                            </tr>
                                          </table>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style="padding:12px 0;border-bottom:1px solid #f4f4f5;">
                                          <table cellpadding="0" cellspacing="0">
                                            <tr>
                                              <td style="width:32px;height:32px;background-color:#f3e8ff;border-radius:50%%;text-align:center;vertical-align:middle;">
                                                <span style="color:#7c3aed;font-size:14px;font-weight:600;">2</span>
                                              </td>
                                              <td style="padding-left:12px;">
                                                <p style="margin:0;color:#09090b;font-size:14px;font-weight:500;">Shipped</p>
                                                <p style="margin:4px 0 0;color:#71717a;font-size:13px;">Your order will be on its way soon</p>
                                              </td>
                                            </tr>
                                          </table>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style="padding:12px 0;">
                                          <table cellpadding="0" cellspacing="0">
                                            <tr>
                                              <td style="width:32px;height:32px;background-color:#f3e8ff;border-radius:50%%;text-align:center;vertical-align:middle;">
                                                <span style="color:#7c3aed;font-size:14px;font-weight:600;">3</span>
                                              </td>
                                              <td style="padding-left:12px;">
                                                <p style="margin:0;color:#09090b;font-size:14px;font-weight:500;">Delivered</p>
                                                <p style="margin:4px 0 0;color:#71717a;font-size:13px;">Enjoy your purchase!</p>
                                              </td>
                                            </tr>
                                          </table>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                
                              <!-- CTA Button -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                                <tr>
                                  <td align="center">
                                    <a href="http://localhost:5173/orders"
                                       style="display:inline-block;background-color:#7c3aed;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:50px;font-size:14px;font-weight:500;">
                                      View Your Order
                                    </a>
                                  </td>
                                </tr>
                              </table>
                
                              <p style="margin:0;color:#71717a;font-size:13px;line-height:1.7;text-align:center;">
                                If you have any questions, reply to this email.<br>
                                We are always happy to help.
                              </p>
                
                            </td>
                          </tr>
                
                          <!-- Footer -->
                          <tr>
                            <td style="background-color:#fafafa;border-top:1px solid #e4e4e7;padding:24px 40px;text-align:center;">
                              <p style="margin:0 0 8px;color:#09090b;font-size:14px;font-weight:500;">
                                arc<span style="color:#7c3aed;">.</span>store
                              </p>
                              <p style="margin:0;color:#a1a1aa;font-size:12px;">
                                This is an automated email. Please do not reply directly to this message.
                              </p>
                              <p style="margin:8px 0 0;color:#a1a1aa;font-size:12px;">
                                © 2026 arc.store. All rights reserved.
                              </p>
                            </td>
                          </tr>
                
                        </table>
                      </td>
                    </tr>
                  </table>
                
                </body>
                </html>
                """.formatted(
                    event.getUserName() != null ? event.getUserName() : "Valued Customer",
                    event.getOrderId(),
                    event.getTotalPrice()
                );

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Order confirmation email sent to {}", event.getUserEmail());

        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}