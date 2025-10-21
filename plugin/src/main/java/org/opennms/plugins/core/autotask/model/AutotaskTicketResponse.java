package com.company.autotask.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutotaskTicketResponse {

    @JsonProperty("item")
    private AutotaskTicket item;

    @JsonProperty("items")
    private List<AutotaskTicket> items;

    @JsonProperty("pageDetails")
    private PageDetails pageDetails;

    // Constructors
    public AutotaskTicketResponse() {}

    // Getters and Setters
    public AutotaskTicket getItem() {
        return item;
    }

    public void setItem(AutotaskTicket item) {
        this.item = item;
    }

    public List<AutotaskTicket> getItems() {
        return items;
    }

    public void setItems(List<AutotaskTicket> items) {
        this.items = items;
    }

    public PageDetails getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(PageDetails pageDetails) {
        this.pageDetails = pageDetails;
    }

    // Nested PageDetails class for paginated responses
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageDetails {
        
        @JsonProperty("count")
        private Integer count;

        @JsonProperty("requestCount")
        private Integer requestCount;

        @JsonProperty("prevPageUrl")
        private String prevPageUrl;

        @JsonProperty("nextPageUrl")
        private String nextPageUrl;

        // Constructors
        public PageDetails() {}

        // Getters and Setters
        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(Integer requestCount) {
            this.requestCount = requestCount;
        }

        public String getPrevPageUrl() {
            return prevPageUrl;
        }

        public void setPrevPageUrl(String prevPageUrl) {
            this.prevPageUrl = prevPageUrl;
        }

        public String getNextPageUrl() {
            return nextPageUrl;
        }

        public void setNextPageUrl(String nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PageDetails that = (PageDetails) o;
            return Objects.equals(count, that.count) &&
                    Objects.equals(requestCount, that.requestCount) &&
                    Objects.equals(prevPageUrl, that.prevPageUrl) &&
                    Objects.equals(nextPageUrl, that.nextPageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, requestCount, prevPageUrl, nextPageUrl);
        }

        @Override
        public String toString() {
            return "PageDetails{" +
                    "count=" + count +
                    ", requestCount=" + requestCount +
                    ", prevPageUrl='" + prevPageUrl + '\'' +
                    ", nextPageUrl='" + nextPageUrl + '\'' +
                    '}';
        }
    }

    // Equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutotaskTicketResponse that = (AutotaskTicketResponse) o;
        return Objects.equals(item, that.item) &&
                Objects.equals(items, that.items) &&
                Objects.equals(pageDetails, that.pageDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, items, pageDetails);
    }

    @Override
    public String toString() {
        return "AutotaskTicketResponse{" +
                "item=" + item +
                ", items=" + items +
                ", pageDetails=" + pageDetails +
                '}';
    }
}