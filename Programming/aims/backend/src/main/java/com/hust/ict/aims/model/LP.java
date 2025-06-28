package com.hust.ict.aims.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;

@Entity
@Table(name = "lp")
@DiscriminatorValue("LP")
@JsonTypeName("LP")
public class LP extends MusicDisc {
    // Không cần khai báo lại các trường chung vì đã kế thừa từ MusicDisc
}