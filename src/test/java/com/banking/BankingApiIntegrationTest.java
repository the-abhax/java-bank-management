package com.banking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BankingApiIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void completeBankingFlow() throws Exception {
        register("Alice", "alice@example.com");
        register("Bob", "bob@example.com");
        long alice = createAccount("Alice", "alice@example.com", "1000.00");
        long bob = createAccount("Bob", "bob@example.com", "100.00");

        mvc.perform(post("/api/accounts/{id}/credit", alice).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"200.00\",\"pin\":\"1234\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.account.balance").value(1200.00));

        mvc.perform(post("/api/accounts/{id}/debit", alice).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"50.00\",\"pin\":\"1234\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.account.balance").value(1150.00));

        mvc.perform(post("/api/accounts/{id}/transfer", alice).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"receiverAccountNumber\":" + bob + ",\"amount\":\"150.00\",\"pin\":\"1234\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.account.balance").value(1000.00));

        mvc.perform(get("/api/accounts/{id}", bob))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(250.00));
    }

    @Test
    void rejectsWrongPinAndInsufficientFunds() throws Exception {
        register("Alice", "alice@example.com");
        long account = createAccount("Alice", "alice@example.com", "10.00");
        mvc.perform(post("/api/accounts/{id}/debit", account).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"1.00\",\"pin\":\"9999\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/accounts/{id}/debit", account).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"11.00\",\"pin\":\"1234\"}"))
                .andExpect(status().isBadRequest());
    }

    private void register(String name, String email) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"secret1\"}"))
                .andExpect(status().isCreated());
    }

    private long createAccount(String name, String email, String balance) throws Exception {
        MvcResult result = mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"" + name + "\",\"email\":\"" + email
                                + "\",\"initialBalance\":\"" + balance + "\",\"pin\":\"1234\"}"))
                .andExpect(status().isCreated()).andReturn();
        String json = result.getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\\\"accountNumber\\\":([0-9]+).*", "$1"));
    }
}
