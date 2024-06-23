package org.saflo.ghostNetFishing.model.Entity;

import jakarta.persistence.*;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;

@Entity
@Table(name="GHOSTNET")
public class GhostNet {
    @Id
    @GeneratedValue
    @Column(name="ghost-net_id")
    private Long id;

    @Column(nullable=false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private int estimatedSize;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private GhostNetStatus status = GhostNetStatus.REPORTED;

    @ManyToOne(optional = true)
    @JoinColumn(name = "reported_by_id")
    private Person reportedBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "recovery_pending_by_id")
    private Person recoveryPendingBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "recovered_by_id")
    private Person recoveredBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "losing_reported_by_id")
    private Person losingReportedBy;


    public GhostNet(){}

    public GhostNet(Double latitude, Double longitude, int estimatedSize){
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSize = estimatedSize;
    }

    public GhostNet(Double latitude, Double longitude, int estimatedSize, Person reportedBy){
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSize = estimatedSize;
        this.reportedBy = reportedBy;
    }


    //Getter und Setter
    public Long getId() {return id;}


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(int estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public GhostNetStatus getStatus() {
        return status;
    }

    public void setStatus(GhostNetStatus status) {
        this.status = status;
    }

    public Person getReportedBy() {return reportedBy;}

    public void setReportedBy(Person reportedBy) {this.reportedBy = reportedBy;}

    public Person getRecoveryPendingBy() {
        return recoveryPendingBy;
    }

    public void setRecoveryPendingBy(Person salvagePendingBy) {
        this.recoveryPendingBy = salvagePendingBy;
    }

    public Person getRecoveredBy() {
        return recoveredBy;
    }

    public void setRecoveredBy(Person salvagedBy) {
        this.recoveredBy = salvagedBy;
    }

    public Person getLosingReportedBy() {
        return losingReportedBy;
    }

    public void setLosingReportedBy(Person missingReportedBy) {
        this.losingReportedBy = missingReportedBy;
    }
}
