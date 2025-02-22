package nl.vpro.domain.media;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.XmlValued;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;

import static nl.vpro.domain.media.AVType.AUDIO;
import static nl.vpro.domain.media.AVType.VIDEO;

@XmlEnum
@XmlType(name = "channelEnum")
@JsonSerialize(using = BackwardsCompatibleJsonEnum.Serializer.class)
@JsonDeserialize(using = Channel.Deserializer.class)
public enum Channel implements Displayable, XmlValued {
    @XmlEnumValue("NED1")
    NED1(VIDEO) {
        @Override
        public String toString() {
            return "Nederland 1";
        }

        @Override
        public String misId() {
            return "TV01";
        }

        @Override
        public String pdId() {
            return "NED1";
        }
    },


    @XmlEnumValue("NED2")
    NED2(VIDEO) {
        @Override
        public String toString() {
            return "Nederland 2";
        }

        @Override
        public String misId() {
            return "TV02";
        }

        @Override
        public String pdId() {
            return "NED2";
        }
    },

    @XmlEnumValue("NED3")
    NED3(VIDEO) {
        @Override
        public String toString() {
            return "Nederland 3 & Zapp";
        }

        @Override
        public String misId() {
            return "TV03";
        }

        @Override
        public String pdId() {
            return "NED3";
        }
    },

    @XmlEnumValue("RAD1")
    RAD1(AUDIO) {
        @Override
        public String toString() {
            return "Radio 1";
        }

        @Override
        public String pdId() {
            return "RAD1";
        }
    },

    @XmlEnumValue("RAD2")
    RAD2(AUDIO) {
        @Override
        public String toString() {
            return "Radio 2";
        }

        @Override
        public String pdId() {
            return "RAD2";
        }
    },



    // komt uit prepr
    @XmlEnumValue("R2SJ")
    R2SJ(AUDIO) {
        @Override
        public String toString() {
            return "Radio 2 Soul & Jazz";
        }
    },

    @XmlEnumValue("RAD3")
    RAD3(AUDIO) {
        @Override
        public String toString() {
            return "3FM";
        }

        @Override
        public String pdId() {
            return "RAD3";
        }
    },


    // komt uit prepr
    @XmlEnumValue("R3KX")
    R3KX(AUDIO) {
        @Override
        public String toString() {
            return "Radio 3 KX";
        }
    },

    @XmlEnumValue("RAD4")
    RAD4(AUDIO) {
        @Override
        public String toString() {
            return "Radio 4";
        }

        @Override
        public String pdId() {
            return "RAD4";
        }
    },


    // komt uit prepr
    @XmlEnumValue("R4CO")
    R4CO(AUDIO) {
        @Override
        public String toString() {
            return "NPO Radio 4 Concerten";
        }
    },

    @XmlEnumValue("RAD5")
    RAD5(AUDIO) {
        @Override
        public String toString() {
            return "Radio 5";
        }

    },

    // komt uit prepr
    @XmlEnumValue("R5ST")
    R5ST(AUDIO) {
        @Override
        public String toString() {
            return "NPO Radio 5 Sterren NL";
        }
    },

    @XmlEnumValue("RAD6")
    RAD6(AUDIO) {
        @Override
        public String toString() {
            return "Radio 6";
        }

        @Override
        public String pdId() {
            return "RAD6";
        }
    },

    @XmlEnumValue("RTL4")
    RTL4(VIDEO) {
        @Override
        public String toString() {
            return "RTL 4";
        }
    },

    @XmlEnumValue("RTL5")
    RTL5(VIDEO) {
        @Override
        public String toString() {
            return "RTL 5";
        }
    },

    @XmlEnumValue("SBS6")
    SBS6(VIDEO) {
        @Override
        public String toString() {
            return "SBS 6";
        }
    },

    @XmlEnumValue("RTL7")
    RTL7(VIDEO) {
        @Override
        public String toString() {
            return "RTL 7";
        }
    },

    @XmlEnumValue("VERO")
    VERO(VIDEO) {
        @Override
        public String toString() {
            return "Veronica/Jetix";
        }
    },

    @XmlEnumValue("NET5")
    NET5(VIDEO) {
        @Override
        public String toString() {
            return "Net 5";
        }
    },

    @XmlEnumValue("RTL8")
    RTL8(VIDEO) {
        @Override
        public String toString() {
            return "RTL 8";
        }
    },

    @XmlEnumValue("REGI")
    REGI {
        @Override
        public String toString() {
            return "Regionale TV combikanaal";
        }
    },

    @XmlEnumValue("OFRY")
    OFRY {
        @Override
        public String toString() {
            return "Omrop Fryslan";
        }

        @Override
        public String pdId() {
            return "OFRY";
        }
    },

    @XmlEnumValue("NOOR")
    NOOR {
        @Override
        public String toString() {
            return "TV Noord";
        }

        @Override
        public String pdId() {
            return "NOOR";
        }
    },

    @XmlEnumValue("RTVD")
    RTVD {
        @Override
        public String toString() {
            return "RTV Drenthe";
        }

        @Override
        public String pdId() {
            return "RTVD";
        }
    },

    @XmlEnumValue("OOST")
    OOST {
        @Override
        public String toString() {
            return "TV Oost";
        }

        @Override
        public String pdId() {
            return "OOST";
        }
    },

    @XmlEnumValue("GELD")
    GELD {
        @Override
        public String toString() {
            return "TV Gelderland";
        }

        @Override
        public String pdId() {
            return "GELD";
        }
    },

    @XmlEnumValue("FLEV")
    FLEV {
        @Override
        public String toString() {
            return "TV Flevoland";
        }

        @Override
        public String pdId() {
            return "FLEV";
        }
    },

    @XmlEnumValue("BRAB")
    BRAB {
        @Override
        public String toString() {
            return "Omroep Brabant";
        }

        @Override
        public String pdId() {
            return "BRAB";
        }
    },

    @XmlEnumValue("REGU")
    REGU {
        @Override
        public String toString() {
            return "RTV Utrecht";
        }

        @Override
        public String pdId() {
            return "RTVU";
        }
    },

    @XmlEnumValue("NORH")
    NORH {
        @Override
        public String toString() {
            return "TV Noord-Holland";
        }

        @Override
        public String pdId() {
            return "NORH";
        }
    },

    @XmlEnumValue("WEST")
    WEST {
        @Override
        public String toString() {
            return "TV West";
        }

        @Override
        public String pdId() {
            return "WEST";
        }
    },

    @XmlEnumValue("RIJN")
    RIJN {
        @Override
        public String toString() {
            return "TV Rijnmond";
        }

        @Override
        public String pdId() {
            return "RIJN";
        }
    },

    @XmlEnumValue("L1TV")
    L1TV {
        @Override
        public String toString() {
            return "L1 TV";
        }

        @Override
        public String pdId() {
            return "L1TV";
        }
    },

    @XmlEnumValue("OZEE")
    OZEE {
        @Override
        public String toString() {
            return "Omroep Zeeland";
        }

        @Override
        public String pdId() {
            return "OZEE";
        }
    },
    @XmlEnumValue("TVDR")
    TVDR {
        @Override
        public String toString() {
            return "TV Drenthe";
        }

        @Override
        public String pdId() {
            return "TVDR";
        }
    },
    @XmlEnumValue("AT5_")
    AT5_ {
        @Override
        public String toString() {
            return "AT 5";
        }

        @Override
        public String pdId() {
            return "AT5";
        }
    },

    @XmlEnumValue("RNN7")
    RNN7 {
        @Override
        public String toString() {
            return "RNN7";
        }
    },

    @XmlEnumValue("BVNT")
    BVNT {
        @Override
        public String toString() {
            return "BVN-TV";
        }
    },

    @XmlEnumValue("EEN_")
    EEN_ {
        @Override
        public String toString() {
            return "E\u00E9n";
        }
    },

    @XmlEnumValue("KETN")
    KETN {
        @Override
        public String toString() {
            return "Ketnet"; // Since 2012 zijn Ketnet en Canvas 2 kanalen.
        }
    },

    @XmlEnumValue("VTM_")
    VTM_ {
        @Override
        public String toString() {
            return "VTM";
        }
    },

    @XmlEnumValue("KA2_")
    KA2_ {
        @Override
        public String toString() {
            return "KANAALTWEE";
        }
    },

    @XmlEnumValue("VT4_")
    VT4_ {
        @Override
        public String toString() {
            return "VT4";
        }
    },

    @XmlEnumValue("LUNE")
    LUNE {
        @Override
        public String toString() {
            return "La Une (RTBF 1)";
        }
    },

    @XmlEnumValue("LDUE")
    LDUE {
        @Override
        public String toString() {
            return "La Deux (RTBF 2)";
        }
    },

    @XmlEnumValue("RTBF")
    RTBF {
        @Override
        public String toString() {
            return "RTBF Sat";
        }
    },

    @XmlEnumValue("ARD_")
    ARD_(VIDEO) {
        @Override
        public String toString() {
            return "ARD";
        }
    },

    @XmlEnumValue("ZDF_")
    ZDF_(VIDEO) {
        @Override
        public String toString() {
            return "ZDF";
        }
    },

    @XmlEnumValue("WDR_")
    WDR_(VIDEO) {
        @Override
        public String toString() {
            return "WDR Fernsehen";
        }
    },

    @XmlEnumValue("N_3_")
    N_3_ {
        @Override
        public String toString() {
            return "N3 (NDR)";
        }
    },

    @XmlEnumValue("SUDW")
    SUDW {
        @Override
        public String toString() {
            return "SWF Baden-W\u00FCrttemberg";

        }
    },

    @XmlEnumValue("SWF_")
    SWF_ {
        @Override
        public String toString() {
            return "SWF Rheinland-Pfalz";
        }
    },

    @XmlEnumValue("RTL_")
    RTL_ {
        @Override
        public String toString() {
            return "RTL Television";
        }
    },

    @XmlEnumValue("SAT1")
    SAT1 {
        @Override
        public String toString() {
            return "Sat1";
        }
    },

    @XmlEnumValue("PRO7")
    PRO7 {
        @Override
        public String toString() {
            return "Pro7";
        }
    },

    @XmlEnumValue("3SAT")
    _3SAT {
        @Override
        public String toString() {
            return "3 Sat";
        }

        @Override
        public String misId() {
            return "3SAT";
        }

    },

    @XmlEnumValue("KABE")
    KABE {
        @Override
        public String toString() {
            return "Kabel 1";
        }
    },

    @XmlEnumValue("ARTE")
    ARTE {
        @Override
        public String toString() {
            return "ARTE";
        }
    },

    @XmlEnumValue("T5ME")
    T5ME {
        @Override
        public String toString() {
            return "TV 5 Monde Europe";
        }
    },

    @XmlEnumValue("FRA2")
    FRA2 {
        @Override
        public String toString() {
            return "France 2";
        }
    },

    @XmlEnumValue("FRA3")
    FRA3 {
        @Override
        public String toString() {
            return "France 3";
        }
    },

    @XmlEnumValue("BBC1")
    BBC1 {
        @Override
        public String toString() {
            return "BBC 1";
        }
    },

    @XmlEnumValue("BBC2")
    BBC2 {
        @Override
        public String toString() {
            return "BBC 2";
        }
    },



    @XmlEnumValue("BBTH")
    BBTH {
        @Override
        public String toString() {
            return "BBC Three";
        }
    },

    @XmlEnumValue("BBTC")
    BBTC {
        @Override
        public String toString() {
            return "BBC Three / CBBC";
        }
    },

    @XmlEnumValue("BBCF")
    BBCF {
        @Override
        public String toString() {
            return "BBC Four";
        }
    },

    @XmlEnumValue("BBFC")
    BBFC {
        @Override
        public String toString() {
            return "BBC Four / Ceebies";
        }
    },

    @XmlEnumValue("BBCP")
    BBCP {
        @Override
        public String toString() {
            return "BBC Prime";
        }
    },

    @XmlEnumValue("TRTI")
    TRTI {
        @Override
        public String toString() {
            return "TRT International";
        }
    },

    @XmlEnumValue("SHOW")
    SHOW {
        @Override
        public String toString() {
            return "ShowTV";
        }
    },

    @XmlEnumValue("LIGT")
    LIGT {
        @Override
        public String toString() {
            return "LigTV";
        }
    },

    @XmlEnumValue("TURK")
    TURK {
        @Override
        public String toString() {
            return "Turkmax";
        }
    },
    @XmlEnumValue("ATVT")
    ATVT {
        @Override
        public String toString() {
            return "ATV";
        }
    },

    @XmlEnumValue("RRTM")
    RRTM {
        @Override
        public String toString() {
            return "RTM";
        }
    },

    @XmlEnumValue("RMBC")
    RMBC {
        @Override
        public String toString() {
            return "MBC";
        }
    },

    @XmlEnumValue("RART")
    RART {
        @Override
        public String toString() {
            return "ART Europe";
        }
    },

    @XmlEnumValue("ARTM")
    ARTM {
        @Override
        public String toString() {
            return "ART Movie";
        }
    },

    @XmlEnumValue("TVBS")
    TVBS {
        @Override
        public String toString() {
            return "TVBS Europe";
        }
    },

    @XmlEnumValue("ASIA")
    ASIA {
        @Override
        public String toString() {
            return "Sony Ent TV Asia";
        }
    },

    @XmlEnumValue("TIVI")
    TIVI {
        @Override
        public String toString() {
            return "A-Tivi";
        }
    },

    @XmlEnumValue("B4UM")
    B4UM {
        @Override
        public String toString() {
            return "B4U Movies";
        }
    },

    @XmlEnumValue("PCNE")
    PCNE {
        @Override
        public String toString() {
            return "Phoenix CNE";
        }
    },

    @XmlEnumValue("PATN")
    PATN {
        @Override
        public String toString() {
            return "ATN";
        }
    },

    @XmlEnumValue("ZEET")
    ZEET {
        @Override
        public String toString() {
            return "Zee TV";
        }
    },

    @XmlEnumValue("ZEEC")
    ZEEC {
        @Override
        public String toString() {
            return "Zee Cinema";
        }
    },

    @XmlEnumValue("TVE_")
    TVE_ {
        @Override
        public String toString() {
            return "TVE";
        }
    },

    @XmlEnumValue("RAI_")
    RAI_ {
        @Override
        public String toString() {
            return "Rai Uno";
        }
    },

    @XmlEnumValue("RAID")
    RAID {
        @Override
        public String toString() {
            return "Rai Due";
        }
    },

    @XmlEnumValue("RAIT")
    RAIT {
        @Override
        public String toString() {
            return "Rai Tre";
        }
    },

    @XmlEnumValue("TEVE")
    TEVE {
        @Override
        public String toString() {
            return "TeVe Sur";
        }
    },

    @XmlEnumValue("ERTS")
    ERTS {
        @Override
        public String toString() {
            return "ERT Sat";
        }
    },

    @XmlEnumValue("STV_")
    STV_ {
        @Override
        public String toString() {
            return "STV";
        }
    },

    @XmlEnumValue("NTV_")
    NTV_ {
        @Override
        public String toString() {
            return "NTV";
        }
    },

    @XmlEnumValue("NOSJ")
    NOSJ {
        @Override
        public String toString() {
            return "NPO Nieuws";
        }

        @Override
        public String pdId() {
            return "NOSJ";
        }
    },

    @XmlEnumValue("CULT")
    CULT {
        @Override
        public String toString() {
            return "NPO 2 extra";
        }

        @Override
        public String pdId() {
            return "CULT";
        }
    },

    @XmlEnumValue("101_")
    _101_ {
        @Override
        public String toString() {
            return "NPO 101";
        }

        @Override
        public String misId() {
            return "101_";
        }

        @Override
        public String pdId() {
            return "101_";
        }
    },

    @XmlEnumValue("PO24")
    PO24 {
        @Override
        public String toString() {
            return "NPO Politiek";
        }

        @Override
        public String pdId() {
            return "PO24";
        }
    },

    @XmlEnumValue("HILV")
    HILV {
        @Override
        public String toString() {
            return "NPO Best";
        }

        @Override
        public String pdId() {
            return "HILV";
        }
    },

    @XmlEnumValue("HOLL")
    HOLL {
        @Override
        public String toString() {
            return "NPO Doc";
        }

        @Override
        public String pdId() {
            return "HOLL";
        }
    },

    @XmlEnumValue("GESC")
    GESC {
        @Override
        public String toString() {
            return "/Geschiedenis";
        }
    },

    @XmlEnumValue("3VCN")
    _3VCN {
        @Override
        public String toString() {
            return "3voor12 Central";
        }
    },

    @XmlEnumValue("3VOS")
    _3VOS {
        @Override
        public String toString() {
            return "3voor12 On stage";
        }

        @Override
        public String misId() {
            return "3VOS";
        }
    },

    @XmlEnumValue("NEDE")
    NEDE {
        @Override
        public String toString() {
            return "Nederland-e";
        }
    },

    @XmlEnumValue("STER")
    STER {
        @Override
        public String toString() {
            return "Sterren.nl";
        }
    },

    @XmlEnumValue("OMEG")
    OMEG {
        @Override
        public String toString() {
            return "Omega TV";
        }
    },

    @XmlEnumValue("NCRV")
    NCRV {
        @Override
        public String toString() {
            return "NCRV /Geloven";
        }
    },

    @XmlEnumValue("OPVO")
    OPVO {
        @Override
        public String toString() {
            return "NPO Zapp Xtra / NPO Zappelin Xtra";
        }

        @Override
        public String pdId() {
            return "OPVO";
        }
    },

    @XmlEnumValue("CONS")
    CONS {
        @Override
        public String toString() {
            return "Consumenten TV";
        }
    },

    @XmlEnumValue("HUMO")
    HUMO {
        @Override
        public String toString() {
            return "NPO Humor TV";
        }

        @Override
        public String pdId() {
            return "HUMO";
        }
    },

    @XmlEnumValue("DIER")
    DIER {
        @Override
        public String toString() {
            return "AVRO Dier en Natuur";
        }
    },

    @XmlEnumValue("ENTE")
    ENTE {
        @Override
        public String toString() {
            return "E! Entertainment";
        }
    },

    @XmlEnumValue("FASH")
    FASH {
        @Override
        public String toString() {
            return "Fashion TV";
        }
    },

    @XmlEnumValue("COMC")
    COMC {
        @Override
        public String toString() {
            return "Comedy CentralNickelodeon";
        }
    },
    @XmlEnumValue("COMF")
    COMF {
        @Override
        public String toString() {
            return "Comedy Family";
        }
    },

    @XmlEnumValue("TBN_")
    TBN_ {
        @Override
        public String toString() {
            return "TBN Europe";
        }
    },

    @XmlEnumValue("DISC")
    DISC {
        @Override
        public String toString() {
            return "Discovery Channel";
        }
    },

    @XmlEnumValue("ZONE")
    ZONE {
        @Override
        public String toString() {
            return "Zone Reality (UK)";
        }
    },

    @XmlEnumValue("ANPL")
    ANPL {
        @Override
        public String toString() {
            return "Animal Planet";
        }
    },

    @XmlEnumValue("CLUB")
    CLUB {
        @Override
        public String toString() {
            return "Zone Club";
        }
    },

    @XmlEnumValue("NAGE")
    NAGE {
        @Override
        public String toString() {
            return "National Geographic/CNBC";
        }
    },

    @XmlEnumValue("TRAC")
    TRAC {
        @Override
        public String toString() {
            return "Trace TV";
        }
    },

    @XmlEnumValue("NGHD")
    NGHD {
        @Override
        public String toString() {
            return "National Geographic HD";
        }
    },

    @XmlEnumValue("WILD")
    WILD {
        @Override
        public String toString() {
            return "Nat Geo Wild";
        }
    },

    @XmlEnumValue("GARU")
    GARU {
        @Override
        public String toString() {
            return "Garuda TV";
        }
    },

    @XmlEnumValue("ZAZA")
    ZAZA {
        @Override
        public String toString() {
            return "Zazaro TV";
        }
    },

    @XmlEnumValue("FAM7")
    FAM7 {
        @Override
        public String toString() {
            return "Family7";
        }
    },

    @XmlEnumValue("DTAL")
    DTAL {
        @Override
        public String toString() {
            return "Discovery Travel & Living";
        }
    },

    @XmlEnumValue("SCIE")
    SCIE {
        @Override
        public String toString() {
            return "Discovery Science";
        }
    },

    @XmlEnumValue("CIVI")
    CIVI {
        @Override
        public String toString() {
            return "Discovery Civilisation";
        }
    },

    @XmlEnumValue("DIHD")
    DIHD {
        @Override
        public String toString() {
            return "Discovery HD";
        }
    },

    @XmlEnumValue("HIST")
    HIST {
        @Override
        public String toString() {
            return "The History Channel";
        }
    },

    @XmlEnumValue("TRAV")
    TRAV {
        @Override
        public String toString() {
            return "Travel Channel";
        }
    },

    @XmlEnumValue("HETG")
    HETG {
        @Override
        public String toString() {
            return "Het Gesprek";
        }
    },

    @XmlEnumValue("GOED")
    GOED {
        @Override
        public String toString() {
            return "GoedTV";
        }
    },

    @XmlEnumValue("BABY")
    BABY {
        @Override
        public String toString() {
            return "Baby TV";
        }
    },

    @XmlEnumValue("DH1_")
    DH1_ {
        @Override
        public String toString() {
            return "HD-NL";
        }
    },

    @XmlEnumValue("LITV")
    LITV {
        @Override
        public String toString() {
            return "Liberty TV";
        }
    },

    @XmlEnumValue("LIVE")
    LIVE {
        @Override
        public String toString() {
            return "Liveshop";
        }
    },

    @XmlEnumValue("STAR")
    STAR {
        @Override
        public String toString() {
            return "Star!";
        }
    },

    @XmlEnumValue("WEER")
    WEER {
        @Override
        public String toString() {
            return "Weerkanaal";
        }
    },

    @XmlEnumValue("REAL")
    REAL {
        @Override
        public String toString() {
            return "Zone Reality";
        }
    },

    @XmlEnumValue("SCIF")
    SCIF {
        @Override
        public String toString() {
            return "Sci-Fi Channel";
        }
    },

    @XmlEnumValue("13ST")
    _13ST {
        @Override
        public String toString() {
            return "13Th Street";
        }

        @Override
        public String misId() {
            return "13ST";
        }
    },

    @XmlEnumValue("CARC")
    CARC {
        @Override
        public String toString() {
            return "Car Channel";
        }
    },

    @XmlEnumValue("NOSN")
    NOSN {
        @Override
        public String toString() {
            return "ONS"; // Sinds najaar 2015 wordt Nostalgienet 'ONS'.
            //return "NostalgieNet";
        }
    },

    @XmlEnumValue("HISH")
    HISH {
        @Override
        public String toString() {
            return "The History Channel HD";
        }
    },

    @XmlEnumValue("NICK")
    NICK {
        @Override
        public String toString() {
            return "Nickelodeon";
        }
    },

    @XmlEnumValue("NIJN")
    NIJN {
        @Override
        public String toString() {
            return "Nick Jr.";
        }
    },

    @XmlEnumValue("NIKT")
    NIKT {
        @Override
        public String toString() {
            return "Nick Toons";
        }
    },

    @XmlEnumValue("NIKH")
    NIKH {
        @Override
        public String toString() {
            return "Nick Hits";
        }
    },

    @XmlEnumValue("CART")
    CART {
        @Override
        public String toString() {
            return "Cartoon Network";
        }
    },

    @XmlEnumValue("BOOM")
    BOOM {
        @Override
        public String toString() {
            return "Boomerang";
        }
    },

    @XmlEnumValue("CNN_")
    CNN_ {
        @Override
        public String toString() {
            return "CNN";
        }
    },

    @XmlEnumValue("BBCW")
    BBCW {
        @Override
        public String toString() {
            return "BBC World";
        }
    },

    @XmlEnumValue("EURN")
    EURN {
        @Override
        public String toString() {
            return "Euronews";
        }
    },

    @XmlEnumValue("SKNE")
    SKNE {
        @Override
        public String toString() {
            return "Sky News";
        }
    },

    @XmlEnumValue("BLOO")
    BLOO {
        @Override
        public String toString() {
            return "Bloomberg TV";
        }
    },

    @XmlEnumValue("CNBC")
    CNBC {
        @Override
        public String toString() {
            return "CNBC Europe";
        }
    },

    @XmlEnumValue("PALJ")
    PALJ {
        @Override
        public String toString() {
            return "Al Jazeera Arabisch";
        }
    },

    @XmlEnumValue("ALJA")
    ALJA {
        @Override
        public String toString() {
            return "Al Jazeera";
        }
    },

    @XmlEnumValue("FOXN")
    FOXN {
        @Override
        public String toString() {
            return "Fox News";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("FOXT")
    FOXT {
        @Override
        public String toString() {
            return "Fox Türk";
        }
    },

    @XmlEnumValue("MTV_")
    MTV_ {
        @Override
        public String toString() {
            return "MTV";
        }
    },

    @XmlEnumValue("MTV2")
    MTV2 {
        @Override
        public String toString() {
            return "MTV2";
        }
    },

    @XmlEnumValue("HITS")
    HITS {
        @Override
        public String toString() {
            return "MTV Hits";
        }
    },

    @XmlEnumValue("BASE")
    BASE {
        @Override
        public String toString() {
            return "MTV Base";
        }
    },

    @XmlEnumValue("MTVB")
    MTVB {
        @Override
        public String toString() {
            return "MTV Brand New";
        }
    },

    @XmlEnumValue("TMF_")
    TMF_ {
        @Override
        public String toString() {
            return "TMF";
        }
    },

    @XmlEnumValue("TMFN")
    TMFN {
        @Override
        public String toString() {
            return "TMF NL";
        }
    },

    @XmlEnumValue("TMFP")
    TMFP {
        @Override
        public String toString() {
            return "TMF Party";
        }
    },

    @XmlEnumValue("TMFY")
    TMFY {
        @Override
        public String toString() {
            return "TMF Pure";
        }
    },

    @XmlEnumValue("TVOR")
    TVOR {
        @Override
        public String toString() {
            return "TV Oranje";
        }
    },

    @XmlEnumValue("VH1E")
    VH1E {
        @Override
        public String toString() {
            return "VH-1 (EU)";
        }
    },

    @XmlEnumValue("VH1C")
    VH1C {
        @Override
        public String toString() {
            return "VH-1 Classic";
        }
    },

    @XmlEnumValue("PERC")
    PERC {
        @Override
        public String toString() {
            return "Performance Channel";
        }
    },

    @XmlEnumValue("MEZZ")
    MEZZ {
        @Override
        public String toString() {
            return "Mezzo";
        }
    },

    @XmlEnumValue("EURO")
    EURO {
        @Override
        public String toString() {
            return "Eurosport";
        }
    },

    @XmlEnumValue("EUR2")
    EUR2 {
        @Override
        public String toString() {
            return "Eurosport 2";
        }
    },

    @XmlEnumValue("EXTR")
    EXTR {
        @Override
        public String toString() {
            return "Extreme Sports Channel (EU)";
        }
    },

    @XmlEnumValue("MOTO")
    MOTO {
        @Override
        public String toString() {
            return "Motors TV";
        }
    },

    @XmlEnumValue("SAIL")
    SAIL {
        @Override
        public String toString() {
            return "Sailing channel";
        }
    },

    @XmlEnumValue("ESPN")
    ESPN {
        @Override
        public String toString() {
            return "ESPN Classic Sport";
        }
    },

    @XmlEnumValue("NASE")
    NASE {
        @Override
        public String toString() {
            return "NASN Europe";
        }
    },

    @XmlEnumValue("SP11")
    SP11 {
        @Override
        public String toString() {
            return "Sport1.1";
        }
    },

    @XmlEnumValue("SP12")
    SP12 {
        @Override
        public String toString() {
            return "Sport1.2";
        }
    },

    @XmlEnumValue("SP13")
    SP13 {
        @Override
        public String toString() {
            return "Sport1.3";
        }
    },

    @XmlEnumValue("SP14")
    SP14 {
        @Override
        public String toString() {
            return "Sport1.4";
        }
    },

    @XmlEnumValue("SP15")
    SP15 {
        @Override
        public String toString() {
            return "Sport1.5";
        }
    },

    @XmlEnumValue("SP16")
    SP16 {
        @Override
        public String toString() {
            return "Sport1.6";
        }
    },

    @XmlEnumValue("SP17")
    SP17 {
        @Override
        public String toString() {
            return "Sport1.7";
        }
    },

    @XmlEnumValue("SP18")
    SP18 {
        @Override
        public String toString() {
            return "Sport1.8";
        }
    },

    @XmlEnumValue("S1HD")
    S1HD {
        @Override
        public String toString() {
            return "Sport1 HD";
        }
    },

    @XmlEnumValue("FIL1")
    FIL1 {
        @Override
        public String toString() {
            return "Film1.1";
        }
    },

    @XmlEnumValue("FIL2")
    FIL2 {
        @Override
        public String toString() {
            return "Film1.2";
        }
    },

    @XmlEnumValue("FIL3")
    FIL3 {
        @Override
        public String toString() {
            return "Film1.3";
        }
    },

    @XmlEnumValue("FL11")
    FL11 {
        @Override
        public String toString() {
            return "Film1.1 DI";
        }
    },

    @XmlEnumValue("FL1P")
    FL1P {
        @Override
        public String toString() {
            return "Film1+1 DI";
        }
    },

    @XmlEnumValue("FL12")
    FL12 {
        @Override
        public String toString() {
            return "Film1.2 DI";
        }
    },

    @XmlEnumValue("FL13")
    FL13 {
        @Override
        public String toString() {
            return "Film1.3 DI";
        }
    },

    @XmlEnumValue("FLHD")
    FLHD {
        @Override
        public String toString() {
            return "Film1 HD DI";
        }
    },

    @XmlEnumValue("MGMM")
    MGMM {
        @Override
        public String toString() {
            return "MGM Movie Channel";
        }
    },

    @XmlEnumValue("TCM_")
    TCM_ {
        @Override
        public String toString() {
            return "TCM";
        }
    },

    @XmlEnumValue("HALL")
    HALL {
        @Override
        public String toString() {
            return "Hallmark";
        }
    },
    /**
     * @since 5.11
     */
    @XmlEnumValue("HABN")
    HABN {
        @Override
        public String toString() {
            return "HaberTürk";
        }
    },

    @XmlEnumValue("ACNW")
    ACNW {
        @Override
        public String toString() {
            return "Action Now!";
        }
    },

    @XmlEnumValue("RHUS")
    RHUS {
        @Override
        public String toString() {
            return "Hustler TV";
        }
    },

    @XmlEnumValue("PLAY")
    PLAY {
        @Override
        public String toString() {
            return "Playboy TV";
        }
    },

    @XmlEnumValue("ADUL")
    ADUL {
        @Override
        public String toString() {
            return "Adult Channel";
        }
    },

    @XmlEnumValue("PSPI")
    PSPI {
        @Override
        public String toString() {
            return "Private Spice";
        }
    },

    @XmlEnumValue("HUST")
    HUST {
        @Override
        public String toString() {
            return "Blue Hustler";
        }
    },

    @XmlEnumValue("OXMO")
    OXMO {
        @Override
        public String toString() {
            return "XMO";
        }
    },

    @XmlEnumValue("REGR")
    REGR {
        @Override
        public String toString() {
            return "Regionale radio combikanaal";
        }
    },

    @XmlEnumValue("RFRY")
    RFRY {
        @Override
        public String toString() {
            return "R Omrop Fryslan";
        }

        @Override
        public String pdId() {
            return "RFRY";
        }
    },

    @XmlEnumValue("RNOO")
    RNOO {
        @Override
        public String toString() {
            return "Radio Noord";
        }

        @Override
        public String pdId() {
            return "RNOO";
        }
    },

    @XmlEnumValue("ROST")
    ROST {
        @Override
        public String toString() {
            return "Radio Oost";
        }

        @Override
        public String pdId() {
            return "ROST";
        }
    },

    @XmlEnumValue("RGEL")
    RGEL {
        @Override
        public String toString() {
            return "Radio Gelderland";
        }

        @Override
        public String pdId() {
            return "RGEL";
        }


    },

    @XmlEnumValue("RFLE")
    RFLE {
        @Override
        public String toString() {
            return "Radio Flevoland";
        }

        @Override
        public String pdId() {
            return "RFLE";
        }
    },

    @XmlEnumValue("RBRA")
    RBRA {
        @Override
        public String toString() {
            return "R Omroep Brabant";
        }

        @Override
        public String pdId() {
            return "RBRA";
        }


    },

    @XmlEnumValue("RUTR")
    RUTR {
        @Override
        public String toString() {
            return "Radio M Utrecht";
        }

        @Override
        public String pdId() {
            return "RUTR";
        }
    },

    @XmlEnumValue("RNOH")
    RNOH {
        @Override
        public String toString() {
            return "Radio Noord-Holland";
        }

        @Override
        public String pdId() {
            return "RNOH";
        }
    },

    @XmlEnumValue("RWST")
    RWST {
        @Override
        public String toString() {
            return "89,3 Radio West";
        }

        @Override
        public String pdId() {
            return "RWST";
        }
    },

    @XmlEnumValue("RRIJ")
    RRIJ {
        @Override
        public String toString() {
            return "Radio Rijnmond";
        }

        @Override
        public String pdId() {
            return "RRIJ";
        }
    },

    @XmlEnumValue("LRAD")
    LRAD {
        @Override
        public String toString() {
            return "L1 Radio";
        }

        @Override
        public String pdId() {
            return "LRAD";
        }
    },

    @XmlEnumValue("RZEE")
    RZEE {
        @Override
        public String toString() {
            return "R Omroep Zeeland";
        }

        @Override
        public String pdId() {
            return "RZEE";
        }
    },

    @XmlEnumValue("COMM")
    COMM {
        @Override
        public String toString() {
            return "Commercieelen radio combikanaal";
        }
    },

    @XmlEnumValue("RVER")
    RVER {
        @Override
        public String toString() {
            return "Radio Veronica";
        }
    },

    @XmlEnumValue("SLAM")
    SLAM {
        @Override
        public String toString() {
            return "SLAM! FM";
        }
    },

    @XmlEnumValue("SKYR")
    SKYR {
        @Override
        public String toString() {
            return "Sky Radio";
        }
    },

    @XmlEnumValue("RTLF")
    RTLF {
        @Override
        public String toString() {
            return "RTL FM";
        }
    },

    @XmlEnumValue("BNRN")
    BNRN {
        @Override
        public String toString() {
            return "BNR Nieuwsradio";
        }
    },

    @XmlEnumValue("KINK")
    KINK {
        @Override
        public String toString() {
            return "Kink FM";
        }
    },

    @XmlEnumValue("PCAZ")
    PCAZ {
        @Override
        public String toString() {
            return "CAZ!";
        }
    },

    @XmlEnumValue("QMUS")
    QMUS {
        @Override
        public String toString() {
            return "Q-Music";
        }
    },

    @XmlEnumValue("R538")
    R538 {
        @Override
        public String toString() {
            return "Radio 538";
        }
    },

    @XmlEnumValue("GOLD")
    GOLD {
        @Override
        public String toString() {
            return "Radio 10 Gold";
        }
    },

    @XmlEnumValue("ARRO")
    ARRO {
        @Override
        public String toString() {
            return "Arrow Classic Rock";
        }
    },

    @XmlEnumValue("FUNX")
    FUNX {
        @Override
        public String toString() {
            return "FunX";
        }

        @Override
        public String pdId() {
            return "FUNX";
        }
    },

    @XmlEnumValue("FNXA")
    FNXA {
        @Override
        public String toString() {
            return "FunX Amsterdam";
        }
    },


    @XmlEnumValue("FNXAR")
    FNXAR {
        @Override
        public String toString() {
            return "FunX Arab";
        }
    },


    @XmlEnumValue("FNXDA")
    FNXDA {
        @Override
        public String toString() {
            return "FunX Dance";
        }
    },

    @XmlEnumValue("FNXHH")
    FNXHH {
        @Override
        public String toString() {
            return "FunX HipHop";
        }
    },
     @XmlEnumValue("FNXLA")
    FNXLA {
        @Override
        public String toString() {
            return "FunX Latin";
        }
    },
    @XmlEnumValue("FNXRE")
    FNXRE {
        @Override
        public String toString() {
            return "FunX Raggae";
        }
    },
    @XmlEnumValue("FNXSJ")
    FNXSJ {
        @Override
        public String toString() {
            return "FunX SlowJamz";
        }
    },

    @XmlEnumValue("FNXR")
    FNXR {
        @Override
        public String toString() {
            return "FunX Rotterdam";
        }
    },

    @XmlEnumValue("FNXU")
    FNXU {
        @Override
        public String toString() {
            return "FunX Utrecht";
        }
    },

    @XmlEnumValue("FNXD")
    FNXD {
        @Override
        public String toString() {
            return "FunX Den Haag";
        }
    },

    @XmlEnumValue("CLAS")
    CLAS {
        @Override
        public String toString() {
            return "Classic FM";
        }
    },

    @XmlEnumValue("BEL1")
    BEL1 {
        @Override
        public String toString() {
            return "VRT/Radio 1";
        }
    },

    @XmlEnumValue("BEL2")
    BEL2 {
        @Override
        public String toString() {
            return "VRT/Radio 2";
        }
    },

    @XmlEnumValue("KLAR")
    KLAR(AUDIO) {
        @Override
        public String toString() {
            return "Klara";
        }
    },

    @XmlEnumValue("BBR1")
    BBR1(AUDIO) {
        @Override
        public String toString() {
            return "BBC Radio 1";
        }
    },

    @XmlEnumValue("BBR2")
    BBR2(AUDIO) {
        @Override
        public String toString() {
            return "BBC Radio 2";
        }
    },

    @XmlEnumValue("BBR3")
    BBR3(AUDIO) {
        @Override
        public String toString() {
            return "BBC Radio 3";
        }
    },

    @XmlEnumValue("BBR4")
    BBR4(AUDIO) {
        @Override
        public String toString() {
            return "BBC Radio 4";
        }
    },

    @XmlEnumValue("BBWS")
    BBWS(AUDIO) {
        @Override
        public String toString() {
            return "BBC Worldservice";
        }
    },

    @XmlEnumValue("BBCX")
    BBCX {
        @Override
        public String toString() {
            return "BBC 1XTRA";
        }
    },

    @XmlEnumValue("NDR3")
    NDR3 {
        @Override
        public String toString() {
            return "NDR3";
        }
    },

    @XmlEnumValue("WDR4")
    WDR4 {
        @Override
        public String toString() {
            return "WDR 4";
        }
    },

    @XmlEnumValue("WDR3")
    WDR3 {
        @Override
        public String toString() {
            return "WDR3";
        }
    },

    // TODO Check these codes
    @XmlEnumValue("D24K")
    D24K {
        @Override
        public String toString() {
            return "24Kitchen";
        }

    },
    @XmlEnumValue("H1NL")
    H1NL {
        @Override
        public String toString() {
            return "HBO 1";
        }
    },
    @XmlEnumValue("SYFY")
    SYFY {
        @Override
        public String toString () {
            return "Syfy";
        }
    },

    @XmlEnumValue("SBS9")
    SBS9 {
        @Override
        public String toString() {
            return "SBS 9";
        }
    },

    @XmlEnumValue("DIXD")
    DIXD {
        @Override
        public String toString() {
            return "Disney XD";
        }
    },
    @XmlEnumValue("BRNL")
    BRNL {
        @Override
        public String toString() {
            return "Brava NL";
        }
    },
    @XmlEnumValue("BRHD")
    BRHD {
        @Override
        public String toString() {
            return "Brava HD";
        }
    },
    @XmlEnumValue("FOXL")
    FOXL{
        @Override
        public String toString() {
            return "Fox Live";
        }
    },
    @XmlEnumValue("TLC_")
    TLC_ {
        @Override
        public String toString() {
            return "TLC";
        }
    },

    @XmlEnumValue("VRTC")
    VRTC {
        @Override
        public String toString() {
            return "VRT Canvas"; // Since 2012 zijn Ketnet en Canvas 2 kanalen.
        }
    },

    @XmlEnumValue("BCFS")
    BCFS {
        @Override
        public String toString() {
            return "BBC First";
        }
    },

    @XmlEnumValue("FXNL")
    FXNL {
        @Override
        public String toString () {
            return "Fox Nederland";
        }
    },
    @XmlEnumValue("AMC_")
    AMC_ {
        @Override
        public String toString() {
            return "AMC";
        }
    },
    @XmlEnumValue("FLM1")
    FLM1 {
        @Override
        public String toString() {
            return "Film 1";
        }
    },
    @XmlEnumValue("ZGS1")
    ZGS1 {
        @Override
        public String toString() {
            return "Ziggo Sport";
        }
    },
    @XmlEnumValue("BRTZ")
    BRTZ {
        @Override
        public String toString() {
            return "RTL Z";
        }
    },
    @XmlEnumValue("DVIC")
    DVIC {
        @Override
        public String toString() {
            return "Viceland";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB1")
    DVB1 {
        @Override
        public String toString() {
            return "PO DVB-H 1";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB2")
    DVB2 {
        @Override
        public String toString() {
            return "PO DVB-H 2";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB3")
    DVB3 {
        @Override
        public String toString() {
            return "PO DVB-H 3";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("ZIZO")
    ZIZO {
        @Override
        public String toString() {
            return "PO Zizone TV";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("FANT")
    FANT {
        @Override
        public String toString() {
            return "Fan TV";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("ONL1")
    ONL1(VIDEO) {
        @Override
        public String toString() {
            return "NPO Online 1";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("KPN1")
    KPN1(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 1";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN2")
    KPN2(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 2";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN3")
    KPN3(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 3";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN4")
    KPN4(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 4";
        }
    },
    /**
     * @since 5.11
     */
    @XmlEnumValue("ZONH")
    ZONH(VIDEO) {
        @Override
        public String toString() {
            return "Zone Horror";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("XM24")
    XM24() {
        @Override
        public String toString() {
            return "X-MO DI";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("MNET")
    MNET() {
        @Override
        public String toString() {
            return "Misdaadnet";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("OU24")
    OU24() {
        @Override
        public String toString() {
            return "PO Out TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("POKE")
    POKE() {
        @Override
        public String toString() {
            return "Poker Channel";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("RACW")
    RACW() {
        @Override
        public String toString() {
            return "Raceworld TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("STTV")
    STTV() {
        @Override
        public String toString() {
            return "Star TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("TVPO")
    TVPO() {
        @Override
        public String toString() {
            return "TV Polonia";
        }
    },
       /**
     * @since 5.11
     */
    @XmlEnumValue("VOOM")
    VOOM() {
        @Override
        public String toString() {
            return "Voom HD";
        }
    }
    ;
    private AVType avType;

    Channel(AVType avType) {
        this.avType = avType;
    }
    Channel() {
        this(null);
    }



    public String misId() {
        return name();
    }

    public String pdId() {
        return name();
    }

    public AVType getAVType() {
        return avType;
    }

    @Override
    public String getDisplayName() {
        return toString();
    }

    //@JsonValue (would fix ignored test case, but not backwards compatible)
    public final String getXmlEnumValue() {
        try {
            XmlEnumValue xmlEnumValue = this.getClass().getField(name()).getAnnotation(XmlEnumValue.class);
            return xmlEnumValue != null ? xmlEnumValue.value() : name();
        } catch(NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    public static Channel findByMisId(String misId) {
        for(Channel channel : values()) {
            if(channel.misId().equals(misId)) {
                return channel;
            }
        }
        return null;
    }


    public static Channel findByPDId(String epgId) {
        for(Channel channel : values()) {
            if(epgId.equals(channel.pdId())) {
                return channel;
            }
        }
        return null;
    }

    public static List<Channel> valuesOf(Collection<String> strings) {
        List<Channel> result = new ArrayList<>();
        for(String s : strings) {
            if(Character.isDigit(s.charAt(0))) {
                s = "_" + s;
            }
            StringBuilder sBuilder = new StringBuilder(s);
            while(sBuilder.length() < 4) {
                sBuilder.append("_");
            }
            s = sBuilder.toString();
            result.add(Channel.valueOf(s));
        }
        return result;
    }

    public static Channel[] split(String channels) {
        final String[] values = channels.split("\\s*,\\s*");
        List<Channel> result = new ArrayList<>();
        for(String value : values) {
            if(Character.isDigit(value.charAt(0))) {
                value = "_" + value;
            }
            result.add(Channel.valueOf(value));
        }
        return result.toArray(new Channel[result.size()]);
    }

    public static Channel valueOfIgnoreCase(String channel) {
        if (StringUtils.isBlank(channel)) {
            return null;
        }
        try {
            return valueOf(channel);
        } catch (IllegalArgumentException ignore) {
            return valueOf(channel.toUpperCase());
        }

    }

    public static class Deserializer extends BackwardsCompatibleJsonEnum.Deserializer<Channel> {
        public Deserializer() {
            super(Channel.class);
        }
    }

}
