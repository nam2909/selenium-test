package entities;

import javax.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Nam Nguyen <namnguyen@fortna.com>
 * @since 2/26/2023.
 */
@Data
@Entity
@Table(name = "ESB_AS_CASE_CONTENT_DETAIL")
public class EsbAsCaseContentDetail {
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "ESB_AS_CASE_CONTENT_DETAIL_1_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ESB_AS_CASE_CONTENT_DETAIL_1_GEN", sequenceName = "ESB_AS_CASE_CONTENT_DETAIL_1_SEQ", allocationSize = 1)
    protected Long id;
    public static final String COLUMN_CREATED = "CREATED";
    @Column(
            name = COLUMN_CREATED,
            updatable = false,
            nullable = false
    )
    protected Timestamp created = new Timestamp(System.currentTimeMillis());

    public static final String COLUMN_UPDATED = "UPDATED";
    @Column(
            name = COLUMN_UPDATED,
            nullable = false
    )
    @Version
    protected Timestamp updated = new Timestamp(System.currentTimeMillis());

    @Column(name = "MESSAGE_TYPE")
    protected String messageType;
    @Column
    protected String warehouse;
    @Column
    protected String company;
    @Column
    protected String command;
    @Column(name = "WAVE_ID")
    protected String waveId;
    @Column(name = "HEADER_ID")
    protected Long headerId;
    @Column(name = "PICK_LIST")
    protected String pickList;
    @Column(name = "LPN_NUMBER")
    protected String lpnNumber;
    @Column(name = "ITEM_BAR_CODE")
    protected String itemBarCode;
    @Column
    protected Long quantity;
    @Column(name = "LPN_TYPE")
    protected String lpnType;
    @Column
    protected String category;
    @Column
    protected String active;
    @Column(name = "SUB_CATEGORY")
    protected String subCategory;
    @Column(name = "MERCH_TYPE")
    protected String merchType;
    @Column(name = "PALLET_LPN_NUMBER")
    protected String palletLpnNumber;
}
