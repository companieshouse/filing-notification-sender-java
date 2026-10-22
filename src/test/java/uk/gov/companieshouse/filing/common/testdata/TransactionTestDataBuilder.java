package uk.gov.companieshouse.filing.common.testdata;

import uk.gov.companieshouse.api.model.transaction.Filing;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionTestDataBuilder {

    private String companyName = "Test Company Ltd";
    private String kind = "Kind#subkind";
    private final Map<String, Filing> filings = new HashMap<>();

    public static TransactionTestDataBuilder aTransaction() {
        return new TransactionTestDataBuilder();
    }

    public TransactionTestDataBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public TransactionTestDataBuilder withFilings(Map<String, Filing> filings) {
        this.filings.clear();
        this.filings.putAll(filings);
        return this;
    }

    public TransactionTestDataBuilder withFilingDescription(String key, String description) {
        this.filings.put(key, filingWithDescription(description));
        return this;
    }

    public TransactionTestDataBuilder withKind(String kind) {
        this.kind = kind;
        return this;
    }

    public Transaction build() {
        Transaction transaction = new Transaction();
        transaction.setCompanyName(companyName);
        transaction.setKind(kind);
        transaction.setFilings(new HashMap<>(filings));
        return transaction;
    }

    private static Filing filingWithDescription(String description) {
        Filing filing = new Filing();
        filing.setDescription(description);
        return filing;
    }
}
