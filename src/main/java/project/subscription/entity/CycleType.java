package project.subscription.entity;


import java.time.LocalDate;

public enum CycleType {
    MONTH {
        public LocalDate plus(LocalDate date, int interval) {
            return date.plusMonths(interval);
        }
    },

    YEAR {
        public LocalDate plus(LocalDate date, int interval) {
            return date.plusYears(interval);
        }
    };

    public abstract LocalDate plus(LocalDate date, int interval);
}
