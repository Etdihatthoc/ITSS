package com.hust.ict.aims.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cd")
@DiscriminatorValue("CD")
public class CD extends MusicDisc {
    // Không cần khai báo lại các trường chung
}
