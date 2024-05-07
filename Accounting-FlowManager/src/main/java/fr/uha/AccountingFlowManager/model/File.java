package fr.uha.AccountingFlowManager.model;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fileName;

    private String filePath;

    private String contentType;

    private long size;

    private LocalDateTime uploadDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    private Invoice invoice;

    @PrePersist
    public void setUploadDateTime() {
        this.uploadDateTime = LocalDateTime.now();
    }

}
